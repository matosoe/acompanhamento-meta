package io.github.matosoe.controlehoras.ui.weekly;
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

public class WeeklyCalculatorTest {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static long instant(int day, int hour) { return ZonedDateTime.of(2026, 9, day, hour, 0, 0, 0, ZONE).toInstant().toEpochMilli(); }
    private static CategoryEntity category(long id, String type) { return new CategoryEntity(id, "Categoria " + id, 0xff123456, (int) id, type, 5000, true); }
    private static TimeEntryEntity entry(long category, int fromDay, int fromHour, int toDay, int toHour) { return new TimeEntryEntity(1, category, instant(fromDay, fromHour), instant(toDay, toHour), 0, "", 0, 0); }
    @Test public void fullSundayToSaturdayUses168HoursAndSplitsEntries() {
        WeeklyChartModel result = new WeeklyCalculator().calculate(Collections.singletonList(category(1, "MINIMO")),
                Collections.singletonList(entry(1, 6, 23, 7, 1)), LocalDate.of(2026, 9, 6), 1, LocalDate.of(2026, 9, 13), ZONE);
        WeeklySummary week = result.weeks.get(0); assertEquals(604_800, week.periodSeconds); assertEquals(7200, week.registeredSeconds);
        assertEquals(302_400d, week.categories.get(0).targetSeconds, 0.001d); assertEquals(253, week.sequentialWeek);
    }
    @Test public void partialWeekUsesExactAvailableHoursAndHonorsGoalType() {
        WeeklyChartModel result = new WeeklyCalculator().calculate(Arrays.asList(category(1, CategoryEntity.GOAL_TYPE_MINIMUM), category(2, CategoryEntity.GOAL_TYPE_MAXIMUM)),
                Arrays.asList(entry(1, 13, 0, 13, 12), entry(2, 13, 0, 13, 12)), LocalDate.of(2026, 9, 13), 1, LocalDate.of(2026, 9, 16), ZONE);
        WeeklySummary week = result.weeks.get(0); assertEquals(259_200, week.periodSeconds);
        assertEquals(-86_400d, week.categories.get(0).deviationSeconds, 0.001d);
        assertEquals(86_400d, week.categories.get(1).deviationSeconds, 0.001d); assertTrue(week.categories.get(1).achieved);
    }
}
