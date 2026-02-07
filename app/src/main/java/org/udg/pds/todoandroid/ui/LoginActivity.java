package org.udg.pds.todoandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.ActivityLoginBinding;
import org.udg.pds.todoandroid.ui.viewmodel.LoginViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Setup observers
        observeLoginResult();

        binding.buttonLogin.setOnClickListener(v -> performLogin());
    }

    private void observeLoginResult() {
        viewModel.getLoginResult().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    navigateToMain();
                    break;
                case ERROR:
                    setLoading(false);
                    String errorMsg = resource.message != null && resource.message.contains("Network error")
                            ? getString(R.string.error_network)
                            : getString(R.string.error_login_failed);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
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

        // Trigger login via ViewModel
        viewModel.login(username, password);
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
