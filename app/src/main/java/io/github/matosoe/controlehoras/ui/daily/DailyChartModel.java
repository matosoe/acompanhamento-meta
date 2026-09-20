package io.github.matosoe.controlehoras.ui.daily;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public final class DailyChartModel {
    public final LocalDate start;
    public final LocalDate endExclusive;
    public final List<DailySeries> series;

    public DailyChartModel(LocalDate start, LocalDate endExclusive, List<DailySeries> series) {
        this.start = start;
        this.endExclusive = endExclusive;
        this.series = Collections.unmodifiableList(series);
    }
}
