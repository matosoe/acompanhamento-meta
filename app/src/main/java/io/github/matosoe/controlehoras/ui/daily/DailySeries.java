package io.github.matosoe.controlehoras.ui.daily;

import java.util.Collections;
import java.util.List;

public final class DailySeries {
    public final long categoryId;
    public final String categoryName;
    public final int colorArgb;
    public final List<DailyPoint> points;

    public DailySeries(long categoryId, String categoryName, int colorArgb, List<DailyPoint> points) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.colorArgb = colorArgb;
        this.points = Collections.unmodifiableList(points);
    }
}
