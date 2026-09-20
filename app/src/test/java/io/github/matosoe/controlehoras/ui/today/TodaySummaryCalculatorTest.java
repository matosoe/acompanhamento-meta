package io.github.matosoe.controlehoras.ui.today;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

public class TodaySummaryCalculatorTest {
    @Test public void clipsMidnightEntriesAndCalculatesGapsPerCategory() {
        Map<Long, String> categories = new HashMap<>(); categories.put(1L, "Dormir"); categories.put(2L, "Estudo");
        TodaySummary summary = new TodaySummaryCalculator().calculate(Arrays.asList(
                entry(1, -3_600_000L, 3_600_000L), entry(2, 7_200_000L, 10_800_000L)), 0L, 86_400_000L, categories);
        assertEquals(7_200L, summary.registeredSeconds);
        assertEquals(79_200L, summary.unclassifiedSeconds);
        assertEquals(2, summary.categories.size());
        assertFalse(summary.hasOverlap);
    }

    @Test public void reportsDefensiveOverlapAndTotalAboveOneDay() {
        TodaySummary summary = new TodaySummaryCalculator().calculate(Arrays.asList(
                entry(1, 0L, 86_400_000L), entry(2, 0L, 86_400_000L)), 0L, 86_400_000L, Collections.emptyMap());
        assertTrue(summary.hasOverlap);
        assertTrue(summary.exceedsDay);
        assertEquals(0L, summary.unclassifiedSeconds);
    }

    private TimeEntryEntity entry(long category, long start, long end) {
        return new TimeEntryEntity(category, category, start, end, (end - start) / 1_000L, null, 0L, 0L);
    }
}
