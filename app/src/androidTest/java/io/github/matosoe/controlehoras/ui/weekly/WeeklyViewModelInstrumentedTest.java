package io.github.matosoe.controlehoras.ui.weekly;

import static org.junit.Assert.assertEquals;
import android.app.Application;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WeeklyViewModelInstrumentedTest {
    @Test public void loadsSeededCategoriesAndChangesWeekRange() throws Exception {
        AtomicReference<WeeklyViewModel> model=new AtomicReference<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.set(new WeeklyViewModel((Application) ApplicationProvider.getApplicationContext())));
        assertEquals(4,await(model.get(), 4).chart.weeks.size());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.get().setPeriodMode(WeeklyViewModel.WEEKS_8));
        assertEquals(8,await(model.get(), 8).chart.weeks.size());
    }
    private static WeeklyState await(WeeklyViewModel model, int expectedWeeks) throws Exception {
        CountDownLatch latch=new CountDownLatch(1); AtomicReference<WeeklyState> value=new AtomicReference<>();
        Observer<WeeklyState> observer=state->{if(state!=null&&!state.loading&&state.chart.weeks.size()==expectedWeeks){value.set(state);latch.countDown();}};
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.state().observeForever(observer));
        if(!latch.await(5,TimeUnit.SECONDS)) throw new AssertionError("Estado semanal não carregou.");
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.state().removeObserver(observer)); return value.get();
    }
}
