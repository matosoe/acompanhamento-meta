package io.github.matosoe.controlehoras.ui.chart;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YValueMarker;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import io.github.matosoe.controlehoras.ui.weekly.WeeklyCategoryValue;
import io.github.matosoe.controlehoras.ui.weekly.WeeklyChartMode;
import io.github.matosoe.controlehoras.ui.weekly.WeeklyChartModel;
import io.github.matosoe.controlehoras.ui.weekly.WeeklySummary;

/** AndroidPlot adapter for grouped actual/target bars and weekly deviation lines. */
public final class AndroidPlotWeeklyChartRenderer implements WeeklyChartRenderer {
    private final XYPlot plot;
    public AndroidPlotWeeklyChartRenderer(@NonNull XYPlot plot) { this.plot = plot; }
    @SuppressLint("ClickableViewAccessibility") // AndroidPlot's external XYPlot cannot override performClick; listener invokes it on ACTION_UP.
    @Override public void render(@NonNull WeeklyChartModel chart, @NonNull Set<Long> selected,
                                 @NonNull WeeklyChartMode mode, @NonNull PointListener listener) {
        plot.clear(); plot.removeYMarkers(); int size = chart.weeks.size();
        if (size > 1) plot.setDomainBoundaries(0, size - 1, BoundaryMode.FIXED);
        List<WeeklyCategoryValue> first = size == 0 ? new ArrayList<>() : chart.weeks.get(0).categories;
        long firstId = -1;
        for (WeeklyCategoryValue category : first) {
            if (!selected.contains(category.categoryId)) continue; if (firstId < 0) firstId = category.categoryId;
            List<Number> x = new ArrayList<>(), actual = new ArrayList<>(), target = new ArrayList<>(), deviation = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                WeeklyCategoryValue value = find(chart.weeks.get(i), category.categoryId); x.add(i);
                actual.add(value.actualSeconds / 3600d); target.add(value.targetSeconds / 3600d); deviation.add(value.deviationSeconds / 3600d);
            }
            if (mode == WeeklyChartMode.BARS) {
                plot.addSeries(new SimpleXYSeries(x, actual, category.categoryName + " realizado"), new BarFormatter(category.colorArgb, category.colorArgb));
                plot.addSeries(new SimpleXYSeries(x, target, category.categoryName + " meta"), new BarFormatter(Color.LTGRAY, Color.DKGRAY));
            } else plot.addSeries(new SimpleXYSeries(x, deviation, category.categoryName), new LineAndPointFormatter(category.colorArgb, category.colorArgb, null, null));
        }
        if (mode == WeeklyChartMode.DEVIATION) plot.addMarker(new YValueMarker(0, "0"));
        final long selectedFirst = firstId;
        plot.setOnTouchListener((view, event) -> { if (event.getAction() == MotionEvent.ACTION_UP && selectedFirst >= 0) {
            view.performClick();
            Number x = plot.getXVal(event.getX()); if (x != null) { int index = (int) Math.round(x.doubleValue()); if (index >= 0 && index < size) listener.onPoint(index, selectedFirst); }
        } return true; }); plot.redraw();
    }
    private static WeeklyCategoryValue find(WeeklySummary week, long id) {
        for (WeeklyCategoryValue value : week.categories) if (value.categoryId == id) return value;
        throw new IllegalStateException("Categoria ausente do resumo.");
    }
}
