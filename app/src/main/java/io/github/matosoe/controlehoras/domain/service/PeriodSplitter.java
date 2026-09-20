package io.github.matosoe.controlehoras.domain.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import io.github.matosoe.controlehoras.domain.model.TimeSlice;

public final class PeriodSplitter {
    public List<TimeSlice> split(long startEpochMillis, long endEpochMillis, ZoneId zone) {
        if (endEpochMillis <= startEpochMillis) throw new IllegalArgumentException("Fim deve ser posterior ao início.");
        List<TimeSlice> result = new ArrayList<>(); long cursor = startEpochMillis;
        while (cursor < endEpochMillis) {
            LocalDate date = Instant.ofEpochMilli(cursor).atZone(zone).toLocalDate();
            long next = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
            long sliceEnd = Math.min(next, endEpochMillis);
            result.add(new TimeSlice(date, cursor, sliceEnd)); cursor = sliceEnd;
        }
        return result;
    }
}
