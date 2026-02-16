package org.udg.pds.todoandroid.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.LoginCredentials;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.util.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for authentication operations.
 * Single source of truth for authentication state.
 */
@Singleton
public class AuthRepository {

    private final ApiService apiService;

    @Inject
    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Check if user is authenticated by calling the session check endpoint.
     * @return LiveData with Resource wrapper containing user data if authenticated
     */
    public LiveData<Resource<UserDto>> checkAuthentication() {
        MutableLiveData<Resource<UserDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.checkSession().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call,
                                   @NonNull Response<UserDto> response) {
                if (response.isSuccessful()) {
                    // User is authenticated (200 with body or 204 No Content)
                    result.setValue(Resource.success(response.body()));
                } else {
                    // Not authenticated (401, 403, etc.)
                    result.setValue(Resource.error("Not authenticated", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Perform login with credentials.
     * @param username The username
     * @param password The password
     * @return LiveData with Resource wrapper containing user data on success
     */
    public LiveData<Resource<UserDto>> login(String username, String password) {
        MutableLiveData<Resource<UserDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        LoginCredentials credentials = new LoginCredentials(username, password);
        apiService.login(credentials).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call,
                                   @NonNull Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Login failed", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get current user profile.
     * @return LiveData with Resource wrapper containing user profile data
     */
    public LiveData<Resource<UserDto>> getUserProfile() {
        MutableLiveData<Resource<UserDto>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getUserProfile().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call,
                                   @NonNull Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load profile", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDto> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Perform logout.
     * @return LiveData with Resource wrapper indicating success/failure
     */
    public LiveData<Resource<Void>> logout() {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(null));
                } else {
                    result.setValue(Resource.error("Logout failed", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}

