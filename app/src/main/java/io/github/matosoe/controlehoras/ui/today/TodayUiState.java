package io.github.matosoe.controlehoras.ui.today;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/** Complete, renderable state for the Today screen. */
public final class TodayUiState {
    @NonNull public final LocalDate selectedDate;
    @NonNull public final List<TodayEntryItem> entries;
    public final boolean loading;
    @NonNull public final String errorMessage;
    @Nullable public final TodaySummary summary;

    public TodayUiState(@NonNull LocalDate selectedDate, @NonNull List<TodayEntryItem> entries,
                        boolean loading, @NonNull String errorMessage, @Nullable TodaySummary summary) {
        this.selectedDate = selectedDate;
        this.entries = Collections.unmodifiableList(entries);
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.summary = summary;
    }

    @NonNull public static TodayUiState loading(@NonNull LocalDate date) {
        return new TodayUiState(date, Collections.emptyList(), true, "", null);
    }
}
