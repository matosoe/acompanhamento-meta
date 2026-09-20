package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

/** Computes a daily view from clipped intervals, without duplicating aggregate data in Room. */
public final class TodaySummaryCalculator {
    @NonNull public TodaySummary calculate(@NonNull List<TimeEntryEntity> entries, long dayStart, long dayEnd,
                                            @NonNull Map<Long, String> categoryNames) {
        long registered = 0L, latestEnd = Long.MIN_VALUE;
        boolean overlap = false;
        Map<Long, Long> byCategory = new HashMap<>();
        List<TimeEntryEntity> chronological = new ArrayList<>(entries);
        chronological.sort(Comparator.comparingLong(entry -> entry.startEpochMillis));
        for (TimeEntryEntity entry : chronological) {
            long start = Math.max(dayStart, entry.startEpochMillis), end = Math.min(dayEnd, entry.endEpochMillis);
            if (end <= start) continue;
            if (start < latestEnd) overlap = true;
            latestEnd = Math.max(latestEnd, end);
            long seconds = (end - start) / 1_000L;
            registered += seconds;
            byCategory.merge(entry.categoryId, seconds, Long::sum);
        }
        List<TodaySummary.CategoryTotal> totals = new ArrayList<>();
        for (Map.Entry<Long, Long> total : byCategory.entrySet()) totals.add(new TodaySummary.CategoryTotal(
                categoryNames.getOrDefault(total.getKey(), "Categoria indisponível"), total.getValue()));
        totals.sort(Comparator.comparing(total -> total.name));
        long available = (dayEnd - dayStart) / 1_000L;
        return new TodaySummary(registered, Math.max(0L, available - registered), overlap, registered > available, totals);
    }
}
