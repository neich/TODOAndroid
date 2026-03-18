package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.ActivityNavigationBinding;

import java.util.HashSet;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NavigationActivity extends AppCompatActivity {

    private static final String TAG = "NavigationActivity";
    private ActivityNavigationBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d(TAG, "onCreate started");

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        android.util.Log.d(TAG, "setContentView completed");

        setSupportActionBar(binding.toolbar);
        android.util.Log.d(TAG, "setSupportActionBar completed");

        setupNavigation();
        android.util.Log.d(TAG, "setupNavigation completed");
    }

    private void setupNavigation() {
        android.util.Log.d(TAG, "setupNavigation started");

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        android.util.Log.d(TAG, "navHostFragment: " + (navHostFragment != null ? "found" : "NULL"));

        if (navHostFragment == null) return;

        navController = navHostFragment.getNavController();
        android.util.Log.d(TAG, "navController obtained");

        // Define top-level destinations (bottom nav visible, no Up button)
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.tasksFragment);
        topLevelDestinations.add(R.id.profileFragment);
        topLevelDestinations.add(R.id.groupsFragment);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(topLevelDestinations).build();

        // Single activity toolbar manages title and Up button for all fragments
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Bottom navigation synced with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Hide bottom nav for non-top-level destinations (detail/add screens)
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean isTopLevel = topLevelDestinations.contains(destination.getId());
            binding.bottomNavigation.setVisibility(isTopLevel ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
