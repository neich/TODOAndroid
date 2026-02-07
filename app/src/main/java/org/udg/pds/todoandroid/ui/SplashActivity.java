package org.udg.pds.todoandroid.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.ui.viewmodel.SplashViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    private SplashViewModel viewModel;
    private boolean isCheckingAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen before calling super.onCreate()
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Keep the splash screen visible while checking authentication
        splashScreen.setKeepOnScreenCondition(() -> isCheckingAuth);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        // Observe authentication status
        observeAuthStatus();

        // Check authentication
        viewModel.checkAuthentication();
    }

    private void observeAuthStatus() {
        viewModel.getAuthStatus().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    // Keep splash screen visible
                    break;
                case SUCCESS:
                    isCheckingAuth = false;
                    navigateToMain();
                    break;
                case ERROR:
                    isCheckingAuth = false;
                    if (resource.message != null && resource.message.contains("Network error")) {
                        Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                    navigateToLogin();
                    break;
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
