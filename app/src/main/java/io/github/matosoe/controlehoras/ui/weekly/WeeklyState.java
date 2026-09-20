package io.github.matosoe.controlehoras.ui.weekly;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
public final class WeeklyState {
    public final List<CategoryEntity> categories; public final Set<Long> selectedCategoryIds;
    public final WeeklyChartModel chart; public final List<TimeEntryEntity> entries; public final int selectedWeekIndex;
    public final boolean loading; public final String errorMessage;
    public WeeklyState(List<CategoryEntity> categories, Set<Long> selectedIds, WeeklyChartModel chart,
                       List<TimeEntryEntity> entries, int selectedWeekIndex, boolean loading, String error) {
        this.categories = Collections.unmodifiableList(categories); selectedCategoryIds = Collections.unmodifiableSet(selectedIds);
        this.chart = chart; this.entries = Collections.unmodifiableList(entries); this.selectedWeekIndex = selectedWeekIndex;
        this.loading = loading; errorMessage = error;
    }
}
