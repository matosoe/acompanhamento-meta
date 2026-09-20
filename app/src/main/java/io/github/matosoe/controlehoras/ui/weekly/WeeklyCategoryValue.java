package io.github.matosoe.controlehoras.ui.weekly;
public final class WeeklyCategoryValue {
    public final long categoryId; public final String categoryName; public final int colorArgb;
    public final long actualSeconds; public final double targetSeconds; public final double deviationSeconds;
    public final double actualPercent; public final boolean achieved;
    public WeeklyCategoryValue(long id, String name, int color, long actual, double target, double deviation,
                               double actualPercent, boolean achieved) {
        categoryId = id; categoryName = name; colorArgb = color; actualSeconds = actual; targetSeconds = target;
        deviationSeconds = deviation; this.actualPercent = actualPercent; this.achieved = achieved;
    }
}
