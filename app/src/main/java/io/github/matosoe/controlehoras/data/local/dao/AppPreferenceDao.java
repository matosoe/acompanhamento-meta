package io.github.matosoe.controlehoras.data.local.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;

@Dao
public interface AppPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void put(AppPreferenceEntity preference);

    @Query("SELECT * FROM app_preferences WHERE `key` = :key")
    @Nullable
    AppPreferenceEntity get(String key);
}
