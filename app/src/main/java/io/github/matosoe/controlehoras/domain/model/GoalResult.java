package io.github.matosoe.controlehoras.domain.model;

public final class GoalResult {
    public final long actualSeconds;
    public final long periodSeconds;
    public final int targetBasisPoints;
    public final String goalType;
    public final double targetSeconds;
    public final double actualPercent;
    public final double deviationSeconds;
    public final boolean achieved;

    public GoalResult(long actualSeconds, long periodSeconds, int targetBasisPoints, String goalType,
                      double targetSeconds, double actualPercent, double deviationSeconds, boolean achieved) {
        this.actualSeconds = actualSeconds; this.periodSeconds = periodSeconds;
        this.targetBasisPoints = targetBasisPoints; this.goalType = goalType;
        this.targetSeconds = targetSeconds; this.actualPercent = actualPercent;
        this.deviationSeconds = deviationSeconds; this.achieved = achieved;
    }
}
