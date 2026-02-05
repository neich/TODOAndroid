package org.udg.pds.todoandroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * A CookieJar implementation that persists cookies to SharedPreferences.
 * This directly implements OkHttp's CookieJar interface for better compatibility.
 */
public class PersistentCookieJar implements CookieJar {

    private static final String TAG = "PersistentCookieJar";
    private static final String PREFS_NAME = "CookiePrefs";
    private static final String COOKIES_KEY = "cookies_v2";

    private final SharedPreferences prefs;
    private final Map<String, Cookie> cookies;

    public PersistentCookieJar(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        cookies = new ConcurrentHashMap<>();
        loadCookies();
    }

    private String getCookieKey(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain() + cookie.path();
    }

    private void loadCookies() {
        String cookiesJson = prefs.getString(COOKIES_KEY, null);
        if (cookiesJson == null || cookiesJson.isEmpty()) {
            Log.d(TAG, "No cookies to load");
            return;
        }

        try {
            JSONArray array = new JSONArray(cookiesJson);
            long now = System.currentTimeMillis();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String name = obj.getString("name");
                String value = obj.getString("value");
                String domain = obj.getString("domain");
                String path = obj.optString("path", "/");
                long expiresAt = obj.optLong("expiresAt", Long.MAX_VALUE);
                boolean secure = obj.optBoolean("secure", false);
                boolean httpOnly = obj.optBoolean("httpOnly", false);
                boolean hostOnly = obj.optBoolean("hostOnly", true);

                // Skip expired cookies
                if (expiresAt != Long.MAX_VALUE && expiresAt <= now) {
                    Log.d(TAG, "Cookie expired, skipping: " + name);
                    continue;
                }

                Cookie.Builder builder = new Cookie.Builder()
                        .name(name)
                        .value(value)
                        .path(path)
                        .expiresAt(expiresAt);

                if (hostOnly) {
                    builder.hostOnlyDomain(domain);
                } else {
                    builder.domain(domain);
                }

                if (secure) {
                    builder.secure();
                }
                if (httpOnly) {
                    builder.httpOnly();
                }

                Cookie cookie = builder.build();
                String key = getCookieKey(cookie);
                cookies.put(key, cookie);
                Log.d(TAG, "Loaded cookie: " + name + " = " + value + " for domain: " + domain);
            }
            Log.d(TAG, "Loaded " + cookies.size() + " cookies total");
        } catch (Exception e) {
            Log.e(TAG, "Error loading cookies", e);
        }
    }

    private void saveCookies() {
        try {
            JSONArray array = new JSONArray();

            for (Cookie cookie : cookies.values()) {
                // Don't persist session cookies (those without expiresAt set to MAX_VALUE might be session)
                // Actually, we DO want to persist all cookies for our use case

                JSONObject obj = new JSONObject();
                obj.put("name", cookie.name());
                obj.put("value", cookie.value());
                obj.put("domain", cookie.domain());
                obj.put("path", cookie.path());
                obj.put("expiresAt", cookie.expiresAt());
                obj.put("secure", cookie.secure());
                obj.put("httpOnly", cookie.httpOnly());
                obj.put("hostOnly", cookie.hostOnly());
                array.put(obj);
            }

            prefs.edit().putString(COOKIES_KEY, array.toString()).apply();
            Log.d(TAG, "Saved " + cookies.size() + " cookies");
        } catch (Exception e) {
            Log.e(TAG, "Error saving cookies", e);
        }
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookieList) {
        Log.d(TAG, "saveFromResponse() called for URL: " + url);
        Log.d(TAG, "Received " + cookieList.size() + " cookies");

        for (Cookie cookie : cookieList) {
            Log.d(TAG, "Saving cookie: name=" + cookie.name() +
                       ", value=" + cookie.value() +
                       ", domain=" + cookie.domain() +
                       ", path=" + cookie.path() +
                       ", hostOnly=" + cookie.hostOnly() +
                       ", expiresAt=" + cookie.expiresAt());

            String key = getCookieKey(cookie);
            cookies.put(key, cookie);
        }

        if (!cookieList.isEmpty()) {
            saveCookies();
        }

        Log.d(TAG, "Total cookies now: " + cookies.size());
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        Log.d(TAG, "loadForRequest() called for URL: " + url);
        Log.d(TAG, "Total cookies in store: " + cookies.size());

        List<Cookie> matchingCookies = new ArrayList<>();
        List<String> expiredKeys = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Cookie> entry : cookies.entrySet()) {
            Cookie cookie = entry.getValue();

            // Check expiration
            if (cookie.expiresAt() <= now) {
                Log.d(TAG, "Cookie expired: " + cookie.name());
                expiredKeys.add(entry.getKey());
                continue;
            }

            // Use OkHttp's built-in matching
            if (cookie.matches(url)) {
                matchingCookies.add(cookie);
                Log.d(TAG, "Cookie matched: " + cookie.name() + "=" + cookie.value());
            } else {
                Log.d(TAG, "Cookie did NOT match: " + cookie.name() +
                           ", cookie.domain=" + cookie.domain() +
                           ", cookie.hostOnly=" + cookie.hostOnly() +
                           ", url.host=" + url.host());
            }
        }

        // Clean up expired cookies
        for (String key : expiredKeys) {
            cookies.remove(key);
        }
        if (!expiredKeys.isEmpty()) {
            saveCookies();
        }

        Log.d(TAG, "Returning " + matchingCookies.size() + " cookies for " + url);
        return matchingCookies;
    }

    /**
     * Clear all stored cookies (useful for logout)
     */
    public void clearCookies() {
        cookies.clear();
        prefs.edit().remove(COOKIES_KEY).apply();
        Log.d(TAG, "Cleared all cookies");
    }

    /**
     * Get all cookies (for debugging)
     */
    public List<Cookie> getAllCookies() {
        return new ArrayList<>(cookies.values());
    }
}

