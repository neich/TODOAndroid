package org.udg.pds.todoandroid.activity;

import static androidx.navigation.ui.AppBarConfigurationKt.AppBarConfiguration;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
public class NavigationActivity extends AppCompatActivity {

    MainBinding binding;
    NavHostFragment navHostFragment;

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
        DrawerLayout dl = findViewById(R.id.drawer_layout);
        var appBarConfiguration =
            new AppBarConfiguration.Builder(
                R.id.action_home,
                R.id.action_image,
                R.id.action_tasks)
                .setOpenableLayout(dl)
                .build();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settingsFragment) {
            // NavigationUI.onNavDestinationSelected(item, NavHostFragment.findNavController(navHostFragment));
            NavHostFragment.findNavController(navHostFragment).navigate(R.id.settingsFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(int id) {
        NavHostFragment.findNavController(navHostFragment).navigate(R.id.settingsFragment);
    }
}
