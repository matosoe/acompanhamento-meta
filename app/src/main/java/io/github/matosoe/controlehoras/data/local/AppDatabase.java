package io.github.matosoe.controlehoras.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.github.matosoe.controlehoras.data.local.dao.CategoryDao;
import io.github.matosoe.controlehoras.data.local.dao.AppPreferenceDao;
import io.github.matosoe.controlehoras.data.local.dao.TimeEntryDao;
import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

@Database(
        entities = {CategoryEntity.class, TimeEntryEntity.class, AppPreferenceEntity.class},
        version = 1,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "controle_horas.db";
    private static volatile AppDatabase instance;

    public abstract CategoryDao categoryDao();

    public abstract TimeEntryDao timeEntryDao();

    public abstract AppPreferenceDao appPreferenceDao();

    public static AppDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = create(context.getApplicationContext(), DATABASE_NAME);
                }
            }
        }
        return instance;
    }

    static AppDatabase create(@NonNull Context context, @NonNull String databaseName) {
        return Room.databaseBuilder(context, AppDatabase.class, databaseName)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase database) {
                        CategorySeed.seedDatabase(database);
                    }
                })
                .build();
    }
}
