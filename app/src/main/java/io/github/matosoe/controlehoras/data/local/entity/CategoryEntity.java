package io.github.matosoe.controlehoras.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** A fixed category seeded when the local database is created. */
@Entity(
        tableName = "categories",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class CategoryEntity {
    public static final String GOAL_TYPE_MINIMUM = "MINIMO";
    public static final String GOAL_TYPE_MAXIMUM = "MAXIMO";

    @PrimaryKey
    public final long id;

    @NonNull
    public final String name;

    public final int colorArgb;
    public final int displayOrder;

    @NonNull
    public final String goalType;

    public final int targetBasisPoints;
    public final boolean active;

    public CategoryEntity(
            long id,
            @NonNull String name,
            int colorArgb,
            int displayOrder,
            @NonNull String goalType,
            int targetBasisPoints,
            boolean active
    ) {
        this.id = id;
        this.name = name;
        this.colorArgb = colorArgb;
        this.displayOrder = displayOrder;
        this.goalType = goalType;
        this.targetBasisPoints = targetBasisPoints;
        this.active = active;
    }
}
