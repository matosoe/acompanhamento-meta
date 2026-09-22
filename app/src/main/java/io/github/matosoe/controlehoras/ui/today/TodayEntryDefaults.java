package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;

import java.util.List;

/** Calculates default values for a new entry without changing existing intervals. */
final class TodayEntryDefaults {
    private TodayEntryDefaults() { }

    static long startAfterLastEntry(@NonNull List<TodayEntryItem> entries, long dayStartMillis) {
        long latestEnd = dayStartMillis;
        for (TodayEntryItem entry : entries) {
            latestEnd = Math.max(latestEnd, entry.endEpochMillis);
        }
        return latestEnd;
    }
}
