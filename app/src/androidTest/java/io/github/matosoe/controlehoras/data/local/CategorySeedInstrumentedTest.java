package io.github.matosoe.controlehoras.data.local;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class CategorySeedInstrumentedTest {
    @Test
    public void newAndReopenedDatabaseContainsTwelveUniqueSeedCategories() {
        Context context = ApplicationProvider.getApplicationContext();
        String databaseName = "seed-test-" + UUID.randomUUID() + ".db";

        AppDatabase created = AppDatabase.create(context, databaseName);
        assertSeed(created);
        created.close();

        AppDatabase reopened = AppDatabase.create(context, databaseName);
        assertSeed(reopened);
        reopened.close();

        context.deleteDatabase(databaseName);
    }

    private void assertSeed(AppDatabase database) {
        assertEquals(12, database.categoryDao().count());
        assertEquals(10000, database.categoryDao().totalTargetBasisPoints());
    }
}
