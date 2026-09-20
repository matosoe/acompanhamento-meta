package io.github.matosoe.controlehoras.domain.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

public final class WeekCalendar {
    private static final LocalDate EPOCH_SUNDAY = LocalDate.of(2021, 11, 7);
    public LocalDate sundayStart(LocalDate date) { return date.minusDays(date.getDayOfWeek().getValue() % 7); }
    public int sequentialWeek(LocalDate date) { return (int) (Math.floorDiv(sundayStart(date).toEpochDay() - EPOCH_SUNDAY.toEpochDay(), 7) + 1); }
    public long secondsInWeek(LocalDate sunday, ZoneId zone) { return sunday.plusDays(7).atStartOfDay(zone).toEpochSecond() - sunday.atStartOfDay(zone).toEpochSecond(); }
    public DayOfWeek displayStart(boolean mondayFirst) { return mondayFirst ? DayOfWeek.MONDAY : DayOfWeek.SUNDAY; }
}
