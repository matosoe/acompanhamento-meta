package io.github.matosoe.controlehoras.ui.weekly;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.androidplot.xy.XYPlot;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;
import io.github.matosoe.controlehoras.ui.chart.AndroidPlotWeeklyChartRenderer;
import io.github.matosoe.controlehoras.ui.chart.WeeklyChartRenderer;

public final class WeeklyFragment extends Fragment {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final ZoneId zone = ZoneId.systemDefault(); private WeeklyChartMode chartMode = WeeklyChartMode.BARS;
    private WeeklyState current; private WeeklyChartRenderer chart; private WeeklyTableAdapter table;
    private TextView summary, zero, error, chartDetail, detail;
    public WeeklyFragment() { super(R.layout.fragment_weekly); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); WeeklyViewModel model = new ViewModelProvider(this).get(WeeklyViewModel.class);
        Spinner period = view.findViewById(R.id.weekly_period);
        period.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.weekly_periods)));
        period.setSelection(model.periodMode()); period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) { }
            @Override public void onItemSelected(AdapterView<?> parent, View selected, int position, long id) { if (position != model.periodMode()) model.setPeriodMode(position); }
        });
        view.findViewById(R.id.weekly_categories).setOnClickListener(v -> chooseCategories(model));
        ((RadioGroup) view.findViewById(R.id.weekly_mode)).setOnCheckedChangeListener((group, checked) -> { chartMode = checked == R.id.weekly_deviation ? WeeklyChartMode.DEVIATION : WeeklyChartMode.BARS; draw(); });
        summary = view.findViewById(R.id.weekly_summary); zero = view.findViewById(R.id.weekly_zero); error = view.findViewById(R.id.weekly_error);
        chartDetail = view.findViewById(R.id.weekly_chart_detail); detail = view.findViewById(R.id.weekly_detail);
        table = new WeeklyTableAdapter(model::selectWeek); androidx.recyclerview.widget.RecyclerView list = view.findViewById(R.id.weekly_table);
        list.setLayoutManager(new LinearLayoutManager(requireContext())); list.setAdapter(table);
        chart = new AndroidPlotWeeklyChartRenderer((XYPlot) view.findViewById(R.id.weekly_chart));
        model.state().observe(getViewLifecycleOwner(), state -> { current = state; draw(); });
    }
    private void draw() {
        if (current == null || current.loading) return; error.setText(current.errorMessage); zero.setVisibility(chartMode == WeeklyChartMode.DEVIATION ? View.VISIBLE : View.GONE);
        chart.render(current.chart, current.selectedCategoryIds, chartMode, this::showChartPoint); table.submit(tableRows(), tableIndexes());
        if (current.selectedWeekIndex >= 0 && current.selectedWeekIndex < current.chart.weeks.size()) showWeek(current.chart.weeks.get(current.selectedWeekIndex));
    }
    private void showChartPoint(int weekIndex, long categoryId) {
        if (current == null || weekIndex >= current.chart.weeks.size()) return; WeeklySummary week = current.chart.weeks.get(weekIndex);
        WeeklyCategoryValue value = find(week, categoryId); DurationFormatter formatter = new DurationFormatter();
        chartDetail.setText(getString(R.string.weekly_chart_detail, week.sunday.format(DATE), value.categoryName,
                formatter.hhMm(value.actualSeconds), formatter.hhMm(Math.round(value.targetSeconds)),
                signed(formatter.hhMm(Math.round(Math.abs(value.deviationSeconds))), value.deviationSeconds),
                getString(value.achieved ? R.string.goal_achieved : R.string.goal_not_achieved)));
    }
    private void showWeek(WeeklySummary week) {
        DurationFormatter formatter = new DurationFormatter(); summary.setText(getString(R.string.weekly_summary_text,
                week.sunday.format(DATE), week.endExclusive.minusDays(1).format(DATE), week.sequentialWeek, formatter.hhMm(week.registeredSeconds)));
        Map<Long, String> names = new HashMap<>(); for (CategoryEntity item : current.categories) names.put(item.id, item.name);
        StringBuilder lines = new StringBuilder();
        for (TimeEntryEntity entry : current.entries) if (entry.endEpochMillis > week.sunday.atStartOfDay(zone).toInstant().toEpochMilli()
                && entry.startEpochMillis < week.endExclusive.atStartOfDay(zone).toInstant().toEpochMilli()) {
            LocalDate start = Instant.ofEpochMilli(entry.startEpochMillis).atZone(zone).toLocalDate();
            lines.append(start.format(DATE)).append(" · ").append(names.get(entry.categoryId)).append(" · ")
                    .append(Instant.ofEpochMilli(entry.startEpochMillis).atZone(zone).format(DATE_TIME)).append("–")
                    .append(Instant.ofEpochMilli(entry.endEpochMillis).atZone(zone).format(DATE_TIME)).append('\n');
        }
        detail.setText(lines.length() == 0 ? "Nenhum registro nesta semana." : lines.toString().trim());
    }
    private List<String> tableRows() {
        List<String> rows = new ArrayList<>(); if (current == null) return rows; DurationFormatter formatter = new DurationFormatter();
        for (int index = current.chart.weeks.size() - 1; index >= 0; index--) { WeeklySummary week = current.chart.weeks.get(index); StringBuilder row = new StringBuilder("Semana ").append(week.sequentialWeek).append(" · ")
                .append(week.sunday.format(DATE)).append(" a ").append(week.endExclusive.minusDays(1).format(DATE)).append("\nTotal: ").append(formatter.hhMm(week.registeredSeconds));
            for (WeeklyCategoryValue value : week.categories) if (current.selectedCategoryIds.contains(value.categoryId)) row.append("\n").append(value.categoryName).append(": ")
                    .append(formatter.hhMm(value.actualSeconds)).append(" (").append(String.format(java.util.Locale.getDefault(), "%.1f%%", value.actualPercent)).append(") · Meta ")
                    .append(formatter.hhMm(Math.round(value.targetSeconds))).append(" · ").append(signed(formatter.hhMm(Math.round(Math.abs(value.deviationSeconds))), value.deviationSeconds));
            rows.add(row.toString()); }
        return rows;
    }
    private List<Integer> tableIndexes() { List<Integer> indexes = new ArrayList<>(); if (current != null) for (int i = current.chart.weeks.size() - 1; i >= 0; i--) indexes.add(i); return indexes; }
    private void chooseCategories(WeeklyViewModel model) {
        if (current == null) return; String[] names = new String[current.categories.size()]; boolean[] checked = new boolean[names.length];
        for (int i = 0; i < names.length; i++) { CategoryEntity c = current.categories.get(i); names[i] = c.name; checked[i] = current.selectedCategoryIds.contains(c.id); }
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.weekly_categories).setMultiChoiceItems(names, checked, null)
                .setNegativeButton(android.R.string.cancel, null).setPositiveButton(android.R.string.ok, null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> { Set<Long> ids = new HashSet<>(); android.widget.ListView choices = dialog.getListView();
            for (int i = 0; i < choices.getCount(); i++) if (choices.isItemChecked(i)) ids.add(current.categories.get(i).id);
            if (ids.isEmpty()) { Toast.makeText(requireContext(), R.string.weekly_empty_selection, Toast.LENGTH_SHORT).show(); return; } model.setSelectedCategories(ids); dialog.dismiss(); })); dialog.show();
    }
    private static WeeklyCategoryValue find(WeeklySummary week, long id) { for (WeeklyCategoryValue value : week.categories) if (value.categoryId == id) return value; throw new IllegalArgumentException("Categoria ausente"); }
    private static String signed(String value, double number) { return (number >= 0 ? "+" : "-") + value; }
}
