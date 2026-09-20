package io.github.matosoe.controlehoras.ui.daily;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class DailyViewModelInstrumentedTest {
    @Test public void loadsCategoriesAndHonorsSevenDayMode() throws Exception {
        AtomicReference<DailyViewModel> model = new AtomicReference<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.set(new DailyViewModel((Application) ApplicationProvider.getApplicationContext())));
        DailyState initial = await(model.get(), 30); assertEquals(12, initial.categories.size()); assertFalse(initial.errorMessage.length() > 0);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> model.get().setPeriodMode(DailyViewModel.DAYS_7));
        DailyState sevenDays = await(model.get(), 7); assertEquals(7, sevenDays.chart.series.get(0).points.size());
    }
    private static DailyState await(DailyViewModel model, int expectedDays) throws Exception {
        CountDownLatch latch=new CountDownLatch(1); AtomicReference<DailyState> value=new AtomicReference<>();
        Observer<DailyState> observer=state->{if(state!=null&&!state.loading&&state.chart.series.get(0).points.size()==expectedDays){value.set(state);latch.countDown();}};
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.state().observeForever(observer));
        if(!latch.await(5, TimeUnit.SECONDS)) throw new AssertionError("Estado diário não carregou.");
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->model.state().removeObserver(observer)); return value.get();
    }
}
