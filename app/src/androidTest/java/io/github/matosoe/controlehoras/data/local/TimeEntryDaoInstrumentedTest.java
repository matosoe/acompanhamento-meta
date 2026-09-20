package io.github.matosoe.controlehoras.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import io.github.matosoe.controlehoras.data.local.dao.TimeEntryDao;
import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

@RunWith(AndroidJUnit4.class)
public class TimeEntryDaoInstrumentedTest {
    private Context context;
    private String databaseName;
    private AppDatabase database;
    private TimeEntryDao timeEntryDao;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        databaseName = "time-entry-test-" + UUID.randomUUID() + ".db";
        database = AppDatabase.create(context, databaseName);
        timeEntryDao = database.timeEntryDao();
    }

    @After
    public void tearDown() {
        database.close();
        context.deleteDatabase(databaseName);
    }

    @Test
    public void crudFiltersAndOverlapUseHalfOpenIntervalsAndExcludeEditedEntry() {
        long firstId = timeEntryDao.insert(entry(1, 1_000L, 3_000L, "primeiro"));

        assertNull(timeEntryDao.findOverlap(3_000L, 3_500L, 0));
        assertNotNull(timeEntryDao.findOverlap(2_500L, 3_500L, 0));
        assertNull(timeEntryDao.findOverlap(1_000L, 3_000L, firstId));

        long secondId = timeEntryDao.insert(entry(2, 3_000L, 5_000L, "segundo"));

        List<TimeEntryEntity> allEntries = timeEntryDao.getAllChronological();
        assertEquals(2, allEntries.size());
        assertEquals(firstId, allEntries.get(0).id);
        assertEquals(secondId, allEntries.get(1).id);

        List<TimeEntryEntity> categoryOneEntries = timeEntryDao.getOverlappingPeriod(2_000L, 4_000L, 1L);
        assertEquals(1, categoryOneEntries.size());
        assertEquals(firstId, categoryOneEntries.get(0).id);

        TimeEntryEntity changed = entryWithId(firstId, 1, 1_000L, 3_000L, "atualizado");
        assertEquals(1, timeEntryDao.update(changed));
        assertEquals("atualizado", timeEntryDao.getById(firstId).note);
        assertEquals(1, timeEntryDao.delete(changed));
        assertNull(timeEntryDao.getById(firstId));

        database.appPreferenceDao().put(new AppPreferenceEntity("csv_separator", ";"));
        assertEquals(";", database.appPreferenceDao().get("csv_separator").value);
    }

    private TimeEntryEntity entry(long categoryId, long start, long end, String note) {
        return entryWithId(0, categoryId, start, end, note);
    }

    private TimeEntryEntity entryWithId(long id, long categoryId, long start, long end, String note) {
        return new TimeEntryEntity(id, categoryId, start, end, (end - start) / 1_000L, note, 1_000L, 2_000L);
    }
}
