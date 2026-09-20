package io.github.matosoe.controlehoras.ui.goals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.app.Application;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class GoalsViewModelInstrumentedTest {
    @Test public void loadsSeededGoalsAndSwitchesToSundaySaturdayWeek() throws Exception {
        Application app = ApplicationProvider.getApplicationContext();
        AtomicReference<GoalsViewModel> model = new AtomicReference<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.set(new GoalsViewModel(app)));
        GoalsState daily = awaitLoaded(model.get());
        assertEquals(12, daily.rows.size());
        assertEquals(10_000, daily.targetTotal);
        assertFalse(daily.errorMessage.length() > 0);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.get().setMode(GoalsViewModel.WEEK));
        GoalsState weekly = awaitLoaded(model.get());
        assertEquals(7L, ChronoUnit.DAYS.between(weekly.start, weekly.end));
        assertEquals(0, weekly.start.getDayOfWeek().getValue() % 7);
    }

    private GoalsState awaitLoaded(GoalsViewModel model) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<GoalsState> value = new AtomicReference<>();
        Observer<GoalsState> observer = state -> { if (state != null && !state.loading) { value.set(state); latch.countDown(); } };
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.state().observeForever(observer));
        if (!latch.await(5, TimeUnit.SECONDS)) throw new AssertionError("Estado de metas não carregou.");
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.state().removeObserver(observer));
        return value.get();
    }
}
