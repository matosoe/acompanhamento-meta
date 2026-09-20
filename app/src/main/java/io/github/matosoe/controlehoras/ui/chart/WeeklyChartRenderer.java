package io.github.matosoe.controlehoras.ui.chart;
import androidx.annotation.NonNull;
import io.github.matosoe.controlehoras.ui.weekly.WeeklyChartMode;
import io.github.matosoe.controlehoras.ui.weekly.WeeklyChartModel;
public interface WeeklyChartRenderer {
    void render(@NonNull WeeklyChartModel chart, @NonNull java.util.Set<Long> categoryIds,
                @NonNull WeeklyChartMode mode, @NonNull PointListener listener);
    interface PointListener { void onPoint(int weekIndex, long categoryId); }
}
