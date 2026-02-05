package org.udg.pds.todoandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.api.ApiService;
import org.udg.pds.todoandroid.api.LoginCredentials;
import org.udg.pds.todoandroid.api.UserDto;
import org.udg.pds.todoandroid.databinding.ActivityLoginBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String username = binding.editUsername.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty()) {
            binding.editUsername.setError(getString(R.string.error_username_required));
            binding.editUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.editPassword.setError(getString(R.string.error_password_required));
            binding.editPassword.requestFocus();
            return;
        }

        // Show progress and disable button
        setLoading(true);

        LoginCredentials credentials = new LoginCredentials(username, password);
        apiService.login(credentials).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    // Login successful, navigate to main activity
                    Toast.makeText(LoginActivity.this, 
                        getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    // Login failed
                    Toast.makeText(LoginActivity.this, 
                        getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, 
                    getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.buttonLogin.setEnabled(!loading);
        binding.editUsername.setEnabled(!loading);
        binding.editPassword.setEnabled(!loading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
