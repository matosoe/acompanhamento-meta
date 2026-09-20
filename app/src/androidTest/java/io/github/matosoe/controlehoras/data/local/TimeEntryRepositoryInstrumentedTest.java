package io.github.matosoe.controlehoras.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.data.repository.TimeEntryRepository;
import io.github.matosoe.controlehoras.data.repository.TimeEntryValidationException;

@RunWith(AndroidJUnit4.class)
public class TimeEntryRepositoryInstrumentedTest {
    private Context context;
    private String databaseName;
    private AppDatabase database;
    private TimeEntryRepository repository;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        databaseName = "time-entry-repository-test-" + UUID.randomUUID() + ".db";
        database = AppDatabase.create(context, databaseName);
        repository = new TimeEntryRepository(database, Runnable::run);
    }

    @After
    public void tearDown() {
        database.close();
        context.deleteDatabase(databaseName);
    }

    @Test
    public void saveValidatesDurationAndOverlapThenPersistsNormalizedDuration() {
        AtomicReference<Long> savedId = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();

        repository.save(entry(0, 1, 5_000L, 5_000L), callback(savedId, error));
        assertTrue(error.get() instanceof TimeEntryValidationException);

        error.set(null);
        repository.save(entry(0, 1, 1_000L, 3_500L), callback(savedId, error));
        assertNull(error.get());
        assertNotNull(savedId.get());
        assertEquals(2L, database.timeEntryDao().getById(savedId.get()).durationSeconds);

        error.set(null);
        repository.save(entry(0, 2, 3_000L, 4_000L), callback(new AtomicReference<>(), error));
        assertTrue(error.get() instanceof TimeEntryValidationException);

        error.set(null);
        repository.save(entry(savedId.get(), 1, 1_000L, 3_500L), callback(savedId, error));
        assertNull(error.get());
    }

    @Test
    public void retrievalIsExposedThroughRepository() {
        AtomicReference<Long> savedId = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        repository.save(entry(0, 1, 1_000L, 3_000L), callback(savedId, error));

        AtomicReference<List<TimeEntryEntity>> entries = new AtomicReference<>();
        repository.getForPeriod(1_500L, 2_500L, 1L, callback(entries, error));

        assertNull(error.get());
        assertEquals(1, entries.get().size());
        assertEquals(savedId.get().longValue(), entries.get().get(0).id);
    }

    @Test
    public void savedEntryRemainsAfterDatabaseIsReopened() {
        AtomicReference<Long> savedId = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        repository.save(entry(0, 1, 1_000L, 3_000L), callback(savedId, error));
        database.close();
        database = AppDatabase.create(context, databaseName);
        assertNotNull(database.timeEntryDao().getById(savedId.get()));
    }

    private TimeEntryEntity entry(long id, long categoryId, long start, long end) {
        return new TimeEntryEntity(id, categoryId, start, end, 999L, null, 1L, 1L);
    }

    private <T> RepositoryCallback<T> callback(AtomicReference<T> value, AtomicReference<Throwable> error) {
        return new RepositoryCallback<T>() {
            @Override
            public void onSuccess(T result) {
                value.set(result);
            }

            @Override
            public void onError(Throwable throwable) {
                error.set(throwable);
            }
        };
    }
}
