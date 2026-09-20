package io.github.matosoe.controlehoras.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.SystemClock;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.ui.daily.DailyChartCalculator;
import io.github.matosoe.controlehoras.ui.daily.DailyChartModel;

/** Device-level five-year performance guard for the indexed Room query and daily chart transform. */
@RunWith(AndroidJUnit4.class)
public class PerformanceInstrumentedTest {
    private final ZoneId zone = ZoneId.systemDefault();
    private Context context; private String databaseName; private AppDatabase database;
    @Before public void setUp() {
        context = ApplicationProvider.getApplicationContext(); databaseName = "performance-" + UUID.randomUUID() + ".db";
        database = AppDatabase.create(context, databaseName);
        LocalDate start = LocalDate.now().minusYears(5);
        for (LocalDate day = start; day.isBefore(start.plusYears(5)); day = day.plusDays(1)) {
            long from = day.atTime(8, 0).atZone(zone).toInstant().toEpochMilli();
            long to = day.atTime(9, 0).atZone(zone).toInstant().toEpochMilli();
            database.timeEntryDao().insert(new TimeEntryEntity(0, 1, from, to, 3600, null, from, to));
        }
    }
    @After public void tearDown() { database.close(); context.deleteDatabase(databaseName); }
    @Test public void fiveYearsQueryAndDailyChartCompleteWithinTwoSeconds() {
        LocalDate start = LocalDate.now().minusYears(5), end = start.plusYears(5);
        long from = start.atStartOfDay(zone).toInstant().toEpochMilli(), until = end.atStartOfDay(zone).toInstant().toEpochMilli();
        long began = SystemClock.elapsedRealtime();
        List<TimeEntryEntity> entries = database.timeEntryDao().getOverlappingPeriod(from, until, null);
        List<CategoryEntity> categories = database.categoryDao().getAllOrdered();
        DailyChartModel chart = new DailyChartCalculator().calculate(categories, entries, start, end, Collections.singleton(1L), zone);
        long elapsed = SystemClock.elapsedRealtime() - began;
        assertEquals(1_826, entries.size());
        assertEquals(1_826, chart.series.get(0).points.size());
        assertTrue("Consulta e gráfico levaram " + elapsed + " ms", elapsed <= 2_000L);
    }
}
