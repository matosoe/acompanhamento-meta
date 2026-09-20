package io.github.matosoe.controlehoras.ui.export;

import androidx.annotation.NonNull;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

final class ExportEntryItem {
    @NonNull final TimeEntryEntity entry;
    @NonNull final String categoryName;
    @NonNull final String searchable;
    ExportEntryItem(@NonNull TimeEntryEntity entry, @NonNull String categoryName) {
        this.entry = entry; this.categoryName = categoryName; this.searchable = categoryName.toLowerCase(java.util.Locale.ROOT);
    }
}
