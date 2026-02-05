package org.udg.pds.todoandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.UserDto;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkAuthenticationStatus();
    }

    private void checkAuthenticationStatus() {
        apiService.checkSession().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    // User is authenticated (200 with body or 204 No Content)
                    navigateToMain();
                } else {
                    // Not authenticated (401, 403, etc.), go to login
                    navigateToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                // Network error or server not reachable, go to login
                Toast.makeText(SplashActivity.this, 
                    getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
