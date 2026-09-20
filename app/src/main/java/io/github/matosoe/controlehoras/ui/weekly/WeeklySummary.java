package io.github.matosoe.controlehoras.ui.weekly;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
public final class WeeklySummary {
    public final LocalDate sunday; public final LocalDate endExclusive; public final int sequentialWeek;
    public final long periodSeconds; public final long registeredSeconds; public final List<WeeklyCategoryValue> categories;
    public WeeklySummary(LocalDate sunday, LocalDate endExclusive, int number, long periodSeconds, long registered,
                         List<WeeklyCategoryValue> categories) {
        this.sunday = sunday; this.endExclusive = endExclusive; sequentialWeek = number; this.periodSeconds = periodSeconds;
        registeredSeconds = registered; this.categories = Collections.unmodifiableList(categories);
    }
}
