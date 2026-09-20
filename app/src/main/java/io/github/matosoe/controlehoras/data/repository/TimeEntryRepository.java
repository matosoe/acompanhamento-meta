package io.github.matosoe.controlehoras.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.AppExecutors;
import io.github.matosoe.controlehoras.data.local.dao.TimeEntryDao;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

/** Persistence boundary for time entries: validates before writing and serializes each save. */
public final class TimeEntryRepository {
    private final AppDatabase database;
    private final TimeEntryDao timeEntryDao;
    private final Executor executor;

    public TimeEntryRepository(@NonNull AppDatabase database) {
        this(database, AppExecutors.database());
    }

    public TimeEntryRepository(@NonNull AppDatabase database, @NonNull Executor executor) {
        this.database = database;
        this.timeEntryDao = database.timeEntryDao();
        this.executor = executor;
    }

    public void save(@NonNull TimeEntryEntity draft, @NonNull RepositoryCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long persistedId = database.runInTransaction(() -> saveInTransaction(draft));
                callback.onSuccess(persistedId);
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void delete(@NonNull TimeEntryEntity entry, @NonNull RepositoryCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                callback.onSuccess(timeEntryDao.delete(entry));
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void getForPeriod(
            long periodStartEpochMillis,
            long periodEndEpochMillis,
            @Nullable Long categoryId,
            @NonNull RepositoryCallback<List<TimeEntryEntity>> callback
    ) {
        executor.execute(() -> {
            try {
                callback.onSuccess(timeEntryDao.getOverlappingPeriod(
                        periodStartEpochMillis,
                        periodEndEpochMillis,
                        categoryId
                ));
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }

    private long saveInTransaction(TimeEntryEntity draft) {
        if (draft.endEpochMillis <= draft.startEpochMillis) {
            throw new TimeEntryValidationException("O término deve ser posterior ao início.");
        }

        long durationSeconds = (draft.endEpochMillis - draft.startEpochMillis) / 1_000L;
        if (durationSeconds <= 0L) {
            throw new TimeEntryValidationException("A duração deve conter pelo menos um segundo.");
        }

        TimeEntryEntity overlap = timeEntryDao.findOverlap(
                draft.startEpochMillis,
                draft.endEpochMillis,
                draft.id
        );
        if (overlap != null) {
            throw new TimeEntryValidationException("O intervalo se sobrepõe a outro registro.");
        }

        long now = System.currentTimeMillis();
        TimeEntryEntity normalized = new TimeEntryEntity(
                draft.id,
                draft.categoryId,
                draft.startEpochMillis,
                draft.endEpochMillis,
                durationSeconds,
                draft.note,
                draft.id == 0L ? now : draft.createdAtEpochMillis,
                now
        );

        if (normalized.id == 0L) {
            return timeEntryDao.insert(normalized);
        }
        if (timeEntryDao.update(normalized) != 1) {
            throw new TimeEntryValidationException("Registro não encontrado para atualização.");
        }
        return normalized.id;
    }
}
