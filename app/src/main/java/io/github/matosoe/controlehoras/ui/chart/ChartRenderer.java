package io.github.matosoe.controlehoras.ui.chart;

import androidx.annotation.NonNull;
import io.github.matosoe.controlehoras.ui.daily.DailyChartMode;
import io.github.matosoe.controlehoras.ui.daily.DailyChartModel;

/** Boundary that keeps AndroidPlot types out of ViewModels and chart data. */
public interface ChartRenderer {
    void render(@NonNull DailyChartModel model, @NonNull DailyChartMode mode,
                @NonNull PointListener listener);
    interface PointListener { void onPoint(int seriesIndex, int pointIndex); }
}
