package io.github.matosoe.controlehoras.data.local.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** A continuous interval recorded by the user. All instants are epoch milliseconds. */
@Entity(
        tableName = "time_entries",
        foreignKeys = @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.RESTRICT
        ),
        indices = {
                @Index(value = {"startEpochMillis"}),
                @Index(value = {"categoryId", "startEpochMillis"})
        }
)
public class TimeEntryEntity {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    public final long categoryId;
    public final long startEpochMillis;
    public final long endEpochMillis;
    public final long durationSeconds;

    @Nullable
    public final String note;

    public final long createdAtEpochMillis;
    public final long updatedAtEpochMillis;

    public TimeEntryEntity(
            long id,
            long categoryId,
            long startEpochMillis,
            long endEpochMillis,
            long durationSeconds,
            @Nullable String note,
            long createdAtEpochMillis,
            long updatedAtEpochMillis
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.startEpochMillis = startEpochMillis;
        this.endEpochMillis = endEpochMillis;
        this.durationSeconds = durationSeconds;
        this.note = note;
        this.createdAtEpochMillis = createdAtEpochMillis;
        this.updatedAtEpochMillis = updatedAtEpochMillis;
    }
}
