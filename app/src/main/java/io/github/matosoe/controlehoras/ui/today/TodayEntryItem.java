package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;

/** Immutable display model. The Room entity never reaches the view layer directly. */
public final class TodayEntryItem {
    public final long id;
    public final long categoryId;
    public final long startEpochMillis;
    public final long endEpochMillis;
    @NonNull public final String note;
    public final long createdAtEpochMillis;
    @NonNull public final String timeRange;
    @NonNull public final String categoryName;
    @NonNull public final String duration;

    public TodayEntryItem(long id, long categoryId, long startEpochMillis, long endEpochMillis,
                          @NonNull String note, long createdAtEpochMillis, @NonNull String timeRange,
                          @NonNull String categoryName, @NonNull String duration) {
        this.id = id;
        this.categoryId = categoryId;
        this.startEpochMillis = startEpochMillis;
        this.endEpochMillis = endEpochMillis;
        this.note = note;
        this.createdAtEpochMillis = createdAtEpochMillis;
        this.timeRange = timeRange;
        this.categoryName = categoryName;
        this.duration = duration;
    }
}
