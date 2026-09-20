package io.github.matosoe.controlehoras.ui.export;
import androidx.annotation.NonNull;
import java.util.Collections;
import java.util.List;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
final class ExportState {
    @NonNull final List<CategoryEntity> categories; @NonNull final List<ExportEntryItem> entries; @NonNull final String error;
    ExportState(@NonNull List<CategoryEntity> categories, @NonNull List<ExportEntryItem> entries, @NonNull String error) { this.categories=categories; this.entries=entries; this.error=error; }
    static ExportState empty() { return new ExportState(Collections.emptyList(), Collections.emptyList(), ""); }
}
