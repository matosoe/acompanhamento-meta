package io.github.matosoe.controlehoras.data.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Single owner for database work; callers must not create ad-hoc database threads. */
public final class AppExecutors {
    private static final ExecutorService DATABASE_EXECUTOR = Executors.newSingleThreadExecutor();

    private AppExecutors() {
    }

    public static ExecutorService database() {
        return DATABASE_EXECUTOR;
    }

    public static void shutdown() {
        DATABASE_EXECUTOR.shutdown();
    }

    public static boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return DATABASE_EXECUTOR.awaitTermination(timeout, unit);
    }
}
