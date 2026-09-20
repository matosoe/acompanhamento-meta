package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;

/** Immutable display model. The Room entity never reaches the view layer directly. */
public final class TodayEntryItem {
    @NonNull public final String timeRange;
    @NonNull public final String categoryName;
    @NonNull public final String duration;

    public TodayEntryItem(@NonNull String timeRange, @NonNull String categoryName, @NonNull String duration) {
        this.timeRange = timeRange;
        this.categoryName = categoryName;
        this.duration = duration;
    }
}
