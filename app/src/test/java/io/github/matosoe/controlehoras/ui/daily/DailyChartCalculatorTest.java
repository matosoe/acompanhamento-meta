package io.github.matosoe.controlehoras.ui.daily;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

public class DailyChartCalculatorTest {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static long instant(int day, int hour) { return ZonedDateTime.of(2026, 9, day, hour, 0, 0, 0, ZONE).toInstant().toEpochMilli(); }
    private static CategoryEntity category(long id, String type, int points) { return new CategoryEntity(id, "Categoria " + id, 0xff123456, (int) id, type, points, true); }
    private static TimeEntryEntity entry(long category, int fromDay, int fromHour, int toDay, int toHour) {
        return new TimeEntryEntity(1, category, instant(fromDay, fromHour), instant(toDay, toHour), 0, "", 0, 0);
    }
    @Test public void splitsMidnightAndCreatesEveryDay() {
        DailyChartModel result = new DailyChartCalculator().calculate(Collections.singletonList(category(1, "MINIMO", 1000)),
                Collections.singletonList(entry(1, 1, 23, 2, 1)), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3),
                Collections.singleton(1L), ZONE);
        assertEquals(2, result.series.get(0).points.size());
        assertEquals(3600, result.series.get(0).points.get(0).actualSeconds);
        assertEquals(3600, result.series.get(0).points.get(1).actualSeconds);
    }
    @Test public void cumulativeDeviationHonorsMinimumAndMaximum() {
        CategoryEntity minimum = category(1, CategoryEntity.GOAL_TYPE_MINIMUM, 5000);
        CategoryEntity maximum = category(2, CategoryEntity.GOAL_TYPE_MAXIMUM, 5000);
        DailyChartModel result = new DailyChartCalculator().calculate(Arrays.asList(minimum, maximum),
                Arrays.asList(entry(1, 1, 0, 1, 6), entry(2, 1, 0, 1, 6)), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2),
                new java.util.HashSet<>(Arrays.asList(1L, 2L)), ZONE);
        assertEquals(-21_600d, result.series.get(0).points.get(0).deviationSeconds, 0.001d);
        assertEquals(21_600d, result.series.get(1).points.get(0).cumulativeDeviationSeconds, 0.001d);
        assertTrue(result.series.get(0).points.get(0).targetSeconds > 0);
    }
    @Test public void onlySelectedCategoriesBecomeSeries() {
        DailyChartModel result = new DailyChartCalculator().calculate(Arrays.asList(category(1, "MINIMO", 100), category(2, "MINIMO", 100)),
                Collections.emptyList(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2), Collections.singleton(2L), ZONE);
        assertEquals(1, result.series.size()); assertEquals(2L, result.series.get(0).categoryId);
    }
}
