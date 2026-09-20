package io.github.matosoe.controlehoras.ui.goals;
import androidx.annotation.NonNull; import java.time.LocalDate; import java.util.Collections; import java.util.List;
public final class GoalsState {
 @NonNull public final LocalDate start,end; @NonNull public final List<GoalRow> rows; public final long registered,unclassified; public final int targetTotal; public final boolean loading;
 public GoalsState(@NonNull LocalDate start,@NonNull LocalDate end,@NonNull List<GoalRow> rows,long registered,long unclassified,int targetTotal,boolean loading){this.start=start;this.end=end;this.rows=Collections.unmodifiableList(rows);this.registered=registered;this.unclassified=unclassified;this.targetTotal=targetTotal;this.loading=loading;}
}
