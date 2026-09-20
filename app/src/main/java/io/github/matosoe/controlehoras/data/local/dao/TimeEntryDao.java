package io.github.matosoe.controlehoras.data.local.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

@Dao
public interface TimeEntryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(TimeEntryEntity entry);

    @Update
    int update(TimeEntryEntity entry);

    @Delete
    int delete(TimeEntryEntity entry);

    @Query("SELECT * FROM time_entries WHERE id = :id")
    @Nullable
    TimeEntryEntity getById(long id);

    @Query("SELECT * FROM time_entries ORDER BY startEpochMillis ASC, id ASC")
    List<TimeEntryEntity> getAllChronological();

    @Query("SELECT * FROM time_entries "
            + "WHERE (:fromEpochMillis IS NULL OR endEpochMillis > :fromEpochMillis) "
            + "AND (:untilEpochMillis IS NULL OR startEpochMillis < :untilEpochMillis) "
            + "AND (:categoryId IS NULL OR categoryId = :categoryId) "
            + "ORDER BY startEpochMillis ASC, id ASC")
    List<TimeEntryEntity> search(
            @Nullable Long fromEpochMillis,
            @Nullable Long untilEpochMillis,
            @Nullable Long categoryId
    );

    @Query("SELECT * FROM time_entries "
            + "WHERE endEpochMillis > :periodStartEpochMillis "
            + "AND startEpochMillis < :periodEndEpochMillis "
            + "AND (:categoryId IS NULL OR categoryId = :categoryId) "
            + "ORDER BY startEpochMillis ASC, id ASC")
    List<TimeEntryEntity> getOverlappingPeriod(
            long periodStartEpochMillis,
            long periodEndEpochMillis,
            @Nullable Long categoryId
    );

    @Query("SELECT * FROM time_entries "
            + "WHERE startEpochMillis < :candidateEndEpochMillis "
            + "AND endEpochMillis > :candidateStartEpochMillis "
            + "AND id != :excludedEntryId "
            + "ORDER BY startEpochMillis ASC, id ASC LIMIT 1")
    @Nullable
    TimeEntryEntity findOverlap(
            long candidateStartEpochMillis,
            long candidateEndEpochMillis,
            long excludedEntryId
    );
}
