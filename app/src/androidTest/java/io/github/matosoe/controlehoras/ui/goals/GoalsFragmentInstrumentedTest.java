package io.github.matosoe.controlehoras.ui.goals;

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

@RunWith(AndroidJUnit4.class)
public class GoalsFragmentInstrumentedTest {
    @Test public void goalsDestinationShowsPeriodTotalsAndAccessibleList() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                NavHostFragment host = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.nav_host);
                host.getNavController().navigate(R.id.goalsFragment);
            });
            onView(withId(R.id.goals_period)).check(matches(isDisplayed()));
            onView(withId(R.id.goals_total)).check(matches(isDisplayed()));
            onView(withId(R.id.goals_list)).check(matches(isDisplayed()));
        }
    }
}
