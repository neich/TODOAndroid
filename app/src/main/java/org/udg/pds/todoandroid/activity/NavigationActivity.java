package org.udg.pds.todoandroid.activity;

import static androidx.navigation.ui.AppBarConfigurationKt.AppBarConfiguration;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import org.udg.pds.todoandroid.databinding.MainBinding;
import org.udg.pds.todoandroid.fragment.TaskList;

// This is the main activity that contains the bottom navigation
// This class SHOULD NOT BE CHANGED except for very specific features
public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MainBinding binding;
    NavHostFragment navHostFragment;
    DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        setUpNavigation();
    }

    public void setUpNavigation(){
        navHostFragment = (NavHostFragment)getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        var navController = navHostFragment.getNavController();
        drawerLayout = findViewById(R.id.drawer_layout);
        var appBarConfiguration =
            new AppBarConfiguration.Builder(
                R.id.action_home,
                R.id.action_image,
                R.id.action_tasks)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        if(selectedItemId == R.id.action_settings) {
            startActivity(new Intent(NavigationActivity.this, NavDrawerActivity.class));
        }
        else {
            navigateTo(selectedItemId);
        }

        return true;
    }

    public void navigateTo(int id) {
        NavHostFragment.findNavController(navHostFragment).navigate(id);
    }
}
