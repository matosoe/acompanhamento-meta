package io.github.matosoe.controlehoras.data.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T value);

    void onError(Throwable error);
}
