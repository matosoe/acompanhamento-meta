package io.github.matosoe.controlehoras.ui.daily;

import androidx.annotation.NonNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

/** Splits overlapping entries into local calendar days and derives daily chart series. */
public final class DailyChartCalculator {
    @NonNull public DailyChartModel calculate(@NonNull List<CategoryEntity> categories,
                                               @NonNull List<TimeEntryEntity> entries,
                                               @NonNull LocalDate start, @NonNull LocalDate endExclusive,
                                               @NonNull Set<Long> selectedIds, @NonNull ZoneId zone) {
        if (!endExclusive.isAfter(start)) throw new IllegalArgumentException("Período inválido.");
        Map<Long, Map<LocalDate, Long>> actual = new HashMap<>();
        for (TimeEntryEntity entry : entries) {
            LocalDate cursor = start;
            while (cursor.isBefore(endExclusive)) {
                long dayStart = cursor.atStartOfDay(zone).toInstant().toEpochMilli();
                long dayEnd = cursor.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
                long overlap = Math.min(entry.endEpochMillis, dayEnd) - Math.max(entry.startEpochMillis, dayStart);
                if (overlap > 0) actual.computeIfAbsent(entry.categoryId, ignored -> new HashMap<>())
                        .merge(cursor, overlap / 1_000L, Long::sum);
                cursor = cursor.plusDays(1);
            }
        }
        Set<Long> selected = new HashSet<>(selectedIds);
        List<DailySeries> output = new ArrayList<>();
        for (CategoryEntity category : categories) {
            if (!selected.contains(category.id)) continue;
            List<DailyPoint> points = new ArrayList<>();
            double cumulative = 0d;
            for (LocalDate day = start; day.isBefore(endExclusive); day = day.plusDays(1)) {
                long seconds = actual.getOrDefault(category.id, new HashMap<>()).getOrDefault(day, 0L);
                double target = 86_400d * category.targetBasisPoints / 10_000d;
                double deviation = CategoryEntity.GOAL_TYPE_MAXIMUM.equals(category.goalType)
                        ? target - seconds : seconds - target;
                cumulative += deviation;
                points.add(new DailyPoint(day, seconds, target, deviation, cumulative));
            }
            output.add(new DailySeries(category.id, category.name, category.colorArgb, points));
        }
        return new DailyChartModel(start, endExclusive, output);
    }
}
