package io.github.matosoe.controlehoras.ui.daily;

import java.time.LocalDate;

/** Immutable daily values, in seconds, kept independent from the chart library. */
public final class DailyPoint {
    public final LocalDate date;
    public final long actualSeconds;
    public final double targetSeconds;
    public final double deviationSeconds;
    public final double cumulativeDeviationSeconds;

    public DailyPoint(LocalDate date, long actualSeconds, double targetSeconds,
                      double deviationSeconds, double cumulativeDeviationSeconds) {
        this.date = date;
        this.actualSeconds = actualSeconds;
        this.targetSeconds = targetSeconds;
        this.deviationSeconds = deviationSeconds;
        this.cumulativeDeviationSeconds = cumulativeDeviationSeconds;
    }
}
