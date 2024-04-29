package org.udg.pds.todoandroid.activity;

import static android.view.View.GONE;
import static androidx.navigation.ui.AppBarConfigurationKt.AppBarConfiguration;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.NavigationActivityBinding;
import org.udg.pds.todoandroid.fragment.TaskList;

// This is the main activity that contains the bottom navigation
// This class SHOULD NOT BE CHANGED except for very specific features
public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NavigationActivityBinding binding;
    NavHostFragment navHostFragment;
    DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NavigationActivityBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        setUpNavigation();
    }

    public void setUpNavigation() {
        navHostFragment = (NavHostFragment)getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        var navController = navHostFragment.getNavController();
        drawerLayout = findViewById(R.id.drawer_layout);
        // Here you have to add the root destinations
        // (the ones corresponding to the items of the bottom navigation)
        var appBarConfiguration =
            new AppBarConfiguration.Builder(
                R.id.action_home,
                R.id.action_image,
                R.id.action_tasks)
                .setOpenableLayout(drawerLayout)
                .build();
        // NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        // NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.setNavigationItemSelectedListener(this);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            var itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                navigateTo(R.id.action_home);
            } else if (itemId == R.id.action_image) {
                NavHostFragment.findNavController(navHostFragment).navigate(R.id.action_image);
            } else if (itemId == R.id.action_tasks) {
                navigateTo(R.id.action_tasks);
            }
            item.setChecked(true);
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        if (selectedItemId == R.id.action_about) {
            // IMPORTANT: Example of custom action when selecting an item from the Drawer menu
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 2. Chain together various setter methods to set the dialog characteristics.
            builder.setMessage(R.string.about_message).setTitle(R.string.about_title);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User taps OK button.
                }
            });
            // 3. Get the AlertDialog.
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (selectedItemId == R.id.action_settings) {
            navigateTo(selectedItemId);
        }
        else if (selectedItemId == R.id.action_image || selectedItemId == R.id.action_home || selectedItemId == R.id.action_tasks) {
            binding.bottomNavigation.setSelectedItemId(selectedItemId);
        }

        return true;
    }

    public void navigateTo(int id) {
        NavHostFragment.findNavController(navHostFragment).navigate(id);
    }
}
