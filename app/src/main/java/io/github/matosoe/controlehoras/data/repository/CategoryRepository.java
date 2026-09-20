package io.github.matosoe.controlehoras.data.repository;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Executor;

import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.AppExecutors;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;

public final class CategoryRepository {
    private final AppDatabase database;
    private final Executor executor;

    public CategoryRepository(@NonNull AppDatabase database) {
        this(database, AppExecutors.database());
    }

    public CategoryRepository(@NonNull AppDatabase database, @NonNull Executor executor) {
        this.database = database;
        this.executor = executor;
    }

    public void getAllOrdered(@NonNull RepositoryCallback<List<CategoryEntity>> callback) {
        executor.execute(() -> {
            try {
                callback.onSuccess(database.categoryDao().getAllOrdered());
            } catch (Throwable error) {
                callback.onError(error);
            }
        });
    }
}
