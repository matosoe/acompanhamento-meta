package io.github.matosoe.controlehoras.data.repository;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.AppExecutors;
import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;

public final class AppPreferenceRepository {
    private final AppDatabase database;
    private final Executor executor;

    public AppPreferenceRepository(@NonNull AppDatabase database) {
        this(database, AppExecutors.database());
    }

    public AppPreferenceRepository(@NonNull AppDatabase database, @NonNull Executor executor) {
        this.database = database;
        this.executor = executor;
    }

    public void put(@NonNull AppPreferenceEntity preference, @NonNull RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                database.appPreferenceDao().put(preference);
                callback.onSuccess(null);
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void get(@NonNull String key, @NonNull RepositoryCallback<AppPreferenceEntity> callback) {
        executor.execute(() -> {
            try {
                callback.onSuccess(database.appPreferenceDao().get(key));
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }
}
