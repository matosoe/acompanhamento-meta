package io.github.matosoe.controlehoras.domain.model;

import java.time.LocalDate;

public final class TimeSlice {
    public final LocalDate date;
    public final long startEpochMillis;
    public final long endEpochMillis;
    public final long durationSeconds;

    public TimeSlice(LocalDate date, long startEpochMillis, long endEpochMillis) {
        this.date = date;
        this.startEpochMillis = startEpochMillis;
        this.endEpochMillis = endEpochMillis;
        this.durationSeconds = (endEpochMillis - startEpochMillis) / 1000L;
    }
}
