package io.github.matosoe.controlehoras.domain.service;

import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.domain.model.GoalResult;

public final class GoalCalculator {
    public GoalResult calculate(long actualSeconds, long periodSeconds, int targetBasisPoints, String goalType) {
        if (periodSeconds < 0 || targetBasisPoints < 0) throw new IllegalArgumentException("Valores negativos não são válidos.");
        double target = periodSeconds * (targetBasisPoints / 10000.0);
        double actualPercent = periodSeconds == 0 ? 0 : actualSeconds * 100.0 / periodSeconds;
        double deviation = CategoryEntity.GOAL_TYPE_MAXIMUM.equals(goalType) ? target - actualSeconds : actualSeconds - target;
        return new GoalResult(actualSeconds, periodSeconds, targetBasisPoints, goalType, target, actualPercent, deviation, deviation >= 0);
    }
}
