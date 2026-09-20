package io.github.matosoe.controlehoras.data.local;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.List;

import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;

/** Initial, stable category configuration imported when a database is first created. */
public final class CategorySeed {
    private static final List<CategoryEntity> INITIAL_CATEGORIES = Arrays.asList(
            category(1, "Alimentação", 0xFFE76F51, 1, CategoryEntity.GOAL_TYPE_MINIMUM, 450),
            category(2, "Casa", 0xFFF4A261, 2, CategoryEntity.GOAL_TYPE_MAXIMUM, 1050),
            category(3, "Cuidado", 0xFFE9C46A, 3, CategoryEntity.GOAL_TYPE_MINIMUM, 180),
            category(4, "Desperdício", 0xFFE63946, 4, CategoryEntity.GOAL_TYPE_MINIMUM, 440),
            category(5, "Diversão", 0xFF9B5DE5, 5, CategoryEntity.GOAL_TYPE_MINIMUM, 400),
            category(6, "Dormir", 0xFF457B9D, 6, CategoryEntity.GOAL_TYPE_MINIMUM, 3000),
            category(7, "Estudo", 0xFF2A9D8F, 7, CategoryEntity.GOAL_TYPE_MAXIMUM, 50),
            category(8, "Itaú Estudo", 0xFF00A896, 8, CategoryEntity.GOAL_TYPE_MAXIMUM, 800),
            category(9, "Itaú Trabalho", 0xFF0077B6, 9, CategoryEntity.GOAL_TYPE_MINIMUM, 2650),
            category(10, "Pessoal", 0xFF8338EC, 10, CategoryEntity.GOAL_TYPE_MINIMUM, 810),
            category(11, "Planejamento", 0xFFFFBE0B, 11, CategoryEntity.GOAL_TYPE_MINIMUM, 50),
            category(12, "Treino", 0xFF06D6A0, 12, CategoryEntity.GOAL_TYPE_MAXIMUM, 120)
    );

    private CategorySeed() {
    }

    @NonNull
    public static List<CategoryEntity> initialCategories() {
        return INITIAL_CATEGORIES;
    }

    /** Uses INSERT OR IGNORE so it is safe to run again without duplicating categories. */
    public static void seedDatabase(@NonNull SupportSQLiteDatabase database) {
        for (CategoryEntity category : INITIAL_CATEGORIES) {
            database.execSQL(
                    "INSERT OR IGNORE INTO categories "
                            + "(id, name, colorArgb, displayOrder, goalType, targetBasisPoints, active) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{
                            category.id,
                            category.name,
                            category.colorArgb,
                            category.displayOrder,
                            category.goalType,
                            category.targetBasisPoints,
                            category.active ? 1 : 0
                    }
            );
        }
    }

    private static CategoryEntity category(
            long id,
            String name,
            int colorArgb,
            int displayOrder,
            String goalType,
            int targetBasisPoints
    ) {
        return new CategoryEntity(id, name, colorArgb, displayOrder, goalType, targetBasisPoints, true);
    }
}
