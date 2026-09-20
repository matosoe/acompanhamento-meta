package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;
import java.util.Collections;
import java.util.List;

public final class TodaySummary {
    public final long registeredSeconds;
    public final long unclassifiedSeconds;
    public final boolean hasOverlap;
    public final boolean exceedsDay;
    @NonNull public final List<CategoryTotal> categories;

    TodaySummary(long registeredSeconds, long unclassifiedSeconds, boolean hasOverlap, boolean exceedsDay,
                 @NonNull List<CategoryTotal> categories) {
        this.registeredSeconds = registeredSeconds; this.unclassifiedSeconds = unclassifiedSeconds;
        this.hasOverlap = hasOverlap; this.exceedsDay = exceedsDay; this.categories = Collections.unmodifiableList(categories);
    }

    public static final class CategoryTotal {
        @NonNull public final String name; public final long seconds;
        CategoryTotal(@NonNull String name, long seconds) { this.name = name; this.seconds = seconds; }
    }
}
