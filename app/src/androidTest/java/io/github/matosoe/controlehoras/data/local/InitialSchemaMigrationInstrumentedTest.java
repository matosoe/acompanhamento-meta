package io.github.matosoe.controlehoras.data.local;

import android.content.Context;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class InitialSchemaMigrationInstrumentedTest {
    @Rule
    public final MigrationTestHelper migrationHelper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory()
    );

    @Test
    public void versionOneSchemaCanBeCreatedAndValidated() throws Exception {
        String databaseName = "initial-schema-test-" + UUID.randomUUID() + ".db";
        SupportSQLiteDatabase database = migrationHelper.createDatabase(databaseName, 1);
        database.close();

        migrationHelper.runMigrationsAndValidate(databaseName, 1, true).close();
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(databaseName);
    }
}
