package io.github.matosoe.controlehoras.ui.daily;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;

public final class DailyState {
    public final LocalDate start;
    public final LocalDate endExclusive;
    public final List<CategoryEntity> categories;
    public final Set<Long> selectedCategoryIds;
    public final DailyChartModel chart;
    public final boolean loading;
    public final String errorMessage;

    public DailyState(LocalDate start, LocalDate endExclusive, List<CategoryEntity> categories,
                      Set<Long> selectedCategoryIds, DailyChartModel chart, boolean loading, String errorMessage) {
        this.start = start; this.endExclusive = endExclusive;
        this.categories = Collections.unmodifiableList(categories);
        this.selectedCategoryIds = Collections.unmodifiableSet(selectedCategoryIds);
        this.chart = chart; this.loading = loading; this.errorMessage = errorMessage;
    }
}
