package io.github.matosoe.controlehoras;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        if (host == null) throw new IllegalStateException("NavHost ausente");
        NavController nav = host.getNavController();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        AppBarConfiguration config = new AppBarConfiguration.Builder(
                R.id.todayFragment, R.id.goalsFragment, R.id.dailyFragment,
                R.id.weeklyFragment, R.id.exportFragment, R.id.settingsFragment)
                .setOpenableLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, nav, config);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.navigation_view), nav);
    }
    @Override public boolean onSupportNavigateUp() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        return host != null && host.getNavController().navigateUp() || super.onSupportNavigateUp();
    }
}
