package io.github.matosoe.controlehoras.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.github.matosoe.controlehoras.MainActivity;
import io.github.matosoe.controlehoras.R;

/** Smoke tests for the primary destinations, including text alternatives to charts. */
@RunWith(AndroidJUnit4.class)
public class QualityNavigationInstrumentedTest {
    @Test public void criticalDestinationsAndAccessibleChartTablesAreVisible() {
        assertDestination(R.id.todayFragment, R.id.today_add);
        assertDestination(R.id.dailyFragment, R.id.daily_table);
        assertDestination(R.id.weeklyFragment, R.id.weekly_table);
        assertDestination(R.id.exportFragment, R.id.export_list);
        assertDestination(R.id.settingsFragment, R.id.settings_theme);
    }

    private void assertDestination(int destination, int visibleView) {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                NavHostFragment host = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host);
                host.getNavController().navigate(destination);
            });
            onView(withId(visibleView)).check(matches(isDisplayed()));
        }
    }
}
