package io.github.matosoe.controlehoras.ui.goals;
import androidx.annotation.NonNull;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
public final class GoalRow {
 @NonNull public final CategoryEntity category; @NonNull public final String detail;
 public GoalRow(@NonNull CategoryEntity category,@NonNull String detail){this.category=category;this.detail=detail;}
}
