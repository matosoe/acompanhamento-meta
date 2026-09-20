package io.github.matosoe.controlehoras.domain.service;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

/** Produces the stable, six-column CSV V1 without Android framework dependencies. */
public final class CsvExporter {
    public static final String[] HEADERS = {"Data", "Hora de Início", "Hora de Término", "Semana", "Duração Real", "Projeto"};
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final WeekCalendar weekCalendar;

    public CsvExporter() { this(new WeekCalendar()); }
    public CsvExporter(@NonNull WeekCalendar weekCalendar) { this.weekCalendar = weekCalendar; }

    @NonNull public byte[] export(@NonNull List<CsvRow> rows, char separator) {
        StringBuilder csv = new StringBuilder("\uFEFF");
        append(csv, HEADERS, separator);
        for (CsvRow row : rows) append(csv, new String[]{row.date, row.startTime, row.endTime,
                String.valueOf(row.sequentialWeek), row.duration, row.project}, separator);
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @NonNull public CsvRow row(@NonNull TimeEntryEntity entry, @NonNull String categoryName, @NonNull ZoneId zone) {
        java.time.ZonedDateTime start = Instant.ofEpochMilli(entry.startEpochMillis).atZone(zone);
        java.time.ZonedDateTime end = Instant.ofEpochMilli(entry.endEpochMillis).atZone(zone);
        return new CsvRow(start.toLocalDate().format(DATE), start.format(TIME), end.format(TIME),
                weekCalendar.sequentialWeek(start.toLocalDate()), new DurationFormatter().csv(entry.durationSeconds), categoryName);
    }

    private static void append(StringBuilder target, String[] fields, char separator) {
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) target.append(separator);
            target.append(escape(fields[i], separator));
        }
        target.append("\r\n");
    }

    @NonNull static String escape(@NonNull String value, char separator) {
        boolean quote = value.indexOf(separator) >= 0 || value.indexOf('"') >= 0 || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0;
        return quote ? '"' + value.replace("\"", "\"\"") + '"' : value;
    }

    public static final class CsvRow {
        @NonNull public final String date, startTime, endTime, duration, project;
        public final int sequentialWeek;
        public CsvRow(@NonNull String date, @NonNull String startTime, @NonNull String endTime, int sequentialWeek,
                      @NonNull String duration, @NonNull String project) {
            this.date = date; this.startTime = startTime; this.endTime = endTime; this.sequentialWeek = sequentialWeek;
            this.duration = duration; this.project = project;
        }
    }
}
