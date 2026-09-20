package io.github.matosoe.controlehoras.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<CategoryEntity> categories);

    @Query("SELECT COUNT(*) FROM categories")
    int count();

    @Query("SELECT COALESCE(SUM(targetBasisPoints), 0) FROM categories")
    int totalTargetBasisPoints();

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    List<CategoryEntity> getAllOrdered();
}
