package org.udg.pds.todoandroid.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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

    private ActivityNavigationBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Define top-level destinations (no back button shown)
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.homeFragment);
            topLevelDestinations.add(R.id.tasksFragment);
            topLevelDestinations.add(R.id.profileFragment);

            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(topLevelDestinations).build();

            // Setup toolbar with NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Setup bottom navigation with NavController
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // Listen for destination changes to hide/show bottom nav and toolbar
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int destId = destination.getId();

                // Hide bottom nav and main toolbar for detail fragments (they have their own toolbar)
                if (destId == R.id.addTaskFragment || destId == R.id.taskDetailFragment) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                    binding.appBarLayout.setVisibility(View.GONE);
                    updateFragmentConstraints(true);
                } else {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    binding.appBarLayout.setVisibility(View.VISIBLE);
                    updateFragmentConstraints(false);
                }
            });

            // Ensure the correct item is selected on startup
            binding.bottomNavigation.setSelectedItemId(R.id.homeFragment);
        }
    }

    private void updateFragmentConstraints(boolean fullScreen) {
        ConstraintLayout constraintLayout = (ConstraintLayout) binding.getRoot();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (fullScreen) {
            // Fragment takes full screen
            constraintSet.connect(R.id.navHostFragment, ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(R.id.navHostFragment, ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        } else {
            // Fragment constrained between appbar and bottom nav
            constraintSet.connect(R.id.navHostFragment, ConstraintSet.TOP,
                    R.id.appBarLayout, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.navHostFragment, ConstraintSet.BOTTOM,
                    R.id.bottomNavigation, ConstraintSet.TOP);
        }

        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
