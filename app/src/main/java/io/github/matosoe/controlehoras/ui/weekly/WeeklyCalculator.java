package io.github.matosoe.controlehoras.ui.weekly;

import androidx.annotation.NonNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.domain.service.WeekCalendar;

/** Pure transformation from half-open time intervals to Sunday–Saturday weekly summaries. */
public final class WeeklyCalculator {
    private final WeekCalendar calendar = new WeekCalendar();
    @NonNull public WeeklyChartModel calculate(@NonNull List<CategoryEntity> categories, @NonNull List<TimeEntryEntity> entries,
                                                @NonNull LocalDate firstSunday, int count,
                                                @NonNull LocalDate availableEndExclusive, @NonNull ZoneId zone) {
        if (count <= 0 || !availableEndExclusive.isAfter(firstSunday)) throw new IllegalArgumentException("Período inválido.");
        List<WeeklySummary> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalDate sunday = firstSunday.plusDays(i * 7L);
            LocalDate end = sunday.plusDays(7);
            if (sunday.isBefore(firstSunday) || !sunday.isBefore(availableEndExclusive)) continue;
            LocalDate effectiveEnd = end.isAfter(availableEndExclusive) ? availableEndExclusive : end;
            long from = sunday.atStartOfDay(zone).toInstant().toEpochMilli();
            long until = effectiveEnd.atStartOfDay(zone).toInstant().toEpochMilli();
            long seconds = (until - from) / 1_000L;
            Map<Long, Long> actual = new HashMap<>();
            for (TimeEntryEntity entry : entries) {
                long overlap = Math.min(entry.endEpochMillis, until) - Math.max(entry.startEpochMillis, from);
                if (overlap > 0) actual.merge(entry.categoryId, overlap / 1_000L, Long::sum);
            }
            long registered = actual.values().stream().mapToLong(Long::longValue).sum();
            List<WeeklyCategoryValue> values = new ArrayList<>();
            for (CategoryEntity category : categories) {
                long value = actual.getOrDefault(category.id, 0L);
                double target = seconds * category.targetBasisPoints / 10_000d;
                double deviation = CategoryEntity.GOAL_TYPE_MAXIMUM.equals(category.goalType) ? target - value : value - target;
                values.add(new WeeklyCategoryValue(category.id, category.name, category.colorArgb, value, target, deviation,
                        seconds == 0 ? 0d : value * 100d / seconds, deviation >= 0));
            }
            result.add(new WeeklySummary(sunday, effectiveEnd, calendar.sequentialWeek(sunday), seconds, registered, values));
        }
        return new WeeklyChartModel(result);
    }
}
