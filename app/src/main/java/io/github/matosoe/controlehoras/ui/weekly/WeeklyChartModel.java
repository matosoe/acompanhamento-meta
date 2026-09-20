package io.github.matosoe.controlehoras.ui.weekly;
import java.util.Collections;
import java.util.List;
public final class WeeklyChartModel {
    public final List<WeeklySummary> weeks;
    public WeeklyChartModel(List<WeeklySummary> weeks) { this.weeks = Collections.unmodifiableList(weeks); }
}
