package io.github.matosoe.controlehoras.ui.chart;

import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YValueMarker;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.ui.daily.DailyChartMode;
import io.github.matosoe.controlehoras.ui.daily.DailyChartModel;
import io.github.matosoe.controlehoras.ui.daily.DailyPoint;
import io.github.matosoe.controlehoras.ui.daily.DailySeries;

/** AndroidPlot-only adapter. Point selection is converted back to model indexes. */
public final class AndroidPlotDailyChartRenderer implements ChartRenderer {
    private final XYPlot plot;
    public AndroidPlotDailyChartRenderer(@NonNull XYPlot plot) { this.plot = plot; }

    @Override public void render(@NonNull DailyChartModel model, @NonNull DailyChartMode mode,
                                 @NonNull PointListener listener) {
        plot.clear();
        plot.removeYMarkers();
        int size = model.series.isEmpty() ? 0 : model.series.get(0).points.size();
        if (size > 1) plot.setDomainBoundaries(0, size - 1, BoundaryMode.FIXED);
        for (DailySeries item : model.series) {
            List<Number> x = new ArrayList<>(), y = new ArrayList<>();
            for (int i = 0; i < item.points.size(); i++) {
                DailyPoint point = item.points.get(i);
                x.add(i);
                y.add(mode == DailyChartMode.HOURS ? point.actualSeconds / 3600d
                        : point.cumulativeDeviationSeconds / 3600d);
            }
            LineAndPointFormatter formatter = new LineAndPointFormatter(item.colorArgb, item.colorArgb,
                    null, null);
            plot.addSeries(new SimpleXYSeries(x, y, item.categoryName), formatter);
        }
        if (mode == DailyChartMode.CUMULATIVE_DEVIATION) plot.addMarker(new YValueMarker(0, "0"));
        plot.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && !model.series.isEmpty()) {
                Number x = plot.getXVal(event.getX());
                if (x != null) {
                    int index = (int) Math.round(x.doubleValue());
                    if (index >= 0 && index < size) listener.onPoint(0, index);
                }
            }
            return true;
        });
        plot.redraw();
    }
}
