package io.github.matosoe.controlehoras.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** Key/value storage for local user preferences. */
@Entity(tableName = "app_preferences")
public class AppPreferenceEntity {
    @PrimaryKey
    @NonNull
    public final String key;

    @NonNull
    public final String value;

    public AppPreferenceEntity(@NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }
}
