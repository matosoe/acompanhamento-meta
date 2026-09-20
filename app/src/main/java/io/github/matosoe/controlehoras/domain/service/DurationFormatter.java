package io.github.matosoe.controlehoras.domain.service;

import java.util.Locale;
public final class DurationFormatter {
    public String hhMm(long seconds) { return String.format(Locale.ROOT, "%02d:%02d", seconds / 3600, (seconds % 3600) / 60); }
    public String csv(long seconds) { return String.format(Locale.ROOT, "%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60); }
    public String decimalHours(long seconds) { return String.format(Locale.ROOT, "%.2f", seconds / 3600.0); }
}
