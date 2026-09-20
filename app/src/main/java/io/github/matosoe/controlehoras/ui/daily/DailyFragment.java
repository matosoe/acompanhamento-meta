package io.github.matosoe.controlehoras.ui.daily;

import android.app.DatePickerDialog;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;
import io.github.matosoe.controlehoras.ui.chart.AndroidPlotDailyChartRenderer;
import io.github.matosoe.controlehoras.ui.chart.ChartRenderer;

public final class DailyFragment extends Fragment {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DailyChartMode chartMode = DailyChartMode.CUMULATIVE_DEVIATION;
    private DailyState current;
    private TextView detail, range, categorySummary, error, zero;
    private DailyTableAdapter table;
    private ChartRenderer chart;

    public DailyFragment() { super(R.layout.fragment_daily); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DailyViewModel model = new ViewModelProvider(this).get(DailyViewModel.class);
        Spinner period = view.findViewById(R.id.daily_period);
        period.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.daily_periods)));
        period.setSelection(model.periodMode());
        period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) { }
            @Override public void onItemSelected(AdapterView<?> parent, View selected, int position, long id) {
                if (position != model.periodMode()) model.setPeriodMode(position);
            }
        });
        view.findViewById(R.id.daily_custom).setOnClickListener(v -> chooseCustom(model, period));
        view.findViewById(R.id.daily_categories).setOnClickListener(v -> chooseCategories(model));
        ((RadioGroup) view.findViewById(R.id.daily_mode)).setOnCheckedChangeListener((group, checked) -> {
            chartMode = checked == R.id.daily_hours ? DailyChartMode.HOURS : DailyChartMode.CUMULATIVE_DEVIATION;
            draw();
        });
        detail = view.findViewById(R.id.daily_detail); range = view.findViewById(R.id.daily_range);
        categorySummary = view.findViewById(R.id.daily_categories_summary); error = view.findViewById(R.id.daily_error);
        zero = view.findViewById(R.id.daily_zero);
        table = new DailyTableAdapter();
        androidx.recyclerview.widget.RecyclerView list = view.findViewById(R.id.daily_table);
        list.setLayoutManager(new LinearLayoutManager(requireContext())); list.setAdapter(table);
        chart = new AndroidPlotDailyChartRenderer((XYPlot) view.findViewById(R.id.daily_chart));
        model.state().observe(getViewLifecycleOwner(), screen -> { current = screen; draw(); });
    }
    private void draw() {
        if (current == null || current.loading) return;
        error.setText(current.errorMessage);
        range.setText(getString(R.string.daily_range, current.start.format(DATE), current.endExclusive.minusDays(1).format(DATE)));
        List<String> names = new ArrayList<>();
        for (CategoryEntity category : current.categories) if (current.selectedCategoryIds.contains(category.id)) names.add(category.name);
        categorySummary.setText(String.join(", ", names));
        zero.setVisibility(chartMode == DailyChartMode.CUMULATIVE_DEVIATION ? View.VISIBLE : View.GONE);
        chart.render(current.chart, chartMode, this::showPoint);
        table.submit(tableRows(current.chart));
        if (current.chart.series.isEmpty()) detail.setText(R.string.daily_empty_selection);
    }
    private void showPoint(int series, int point) {
        if (current == null || series >= current.chart.series.size()) return;
        DailySeries item = current.chart.series.get(series); DailyPoint value = item.points.get(point);
        DurationFormatter formatter = new DurationFormatter();
        detail.setText(getString(R.string.daily_detail, value.date.format(DATE), item.categoryName,
                formatter.hhMm(value.actualSeconds), formatter.hhMm(Math.round(value.targetSeconds)),
                signed(formatter.hhMm(Math.round(Math.abs(value.deviationSeconds))), value.deviationSeconds)));
    }
    private List<String> tableRows(DailyChartModel model) {
        List<String> rows = new ArrayList<>(); DurationFormatter formatter = new DurationFormatter();
        for (DailySeries series : model.series) for (DailyPoint point : series.points) rows.add(
                point.date.format(DATE) + " · " + series.categoryName + "\nRealizado: " + formatter.hhMm(point.actualSeconds)
                        + " · Meta: " + formatter.hhMm(Math.round(point.targetSeconds)) + " · Desvio: "
                        + signed(formatter.hhMm(Math.round(Math.abs(point.deviationSeconds))), point.deviationSeconds)
                        + " · Acumulado: " + signed(formatter.hhMm(Math.round(Math.abs(point.cumulativeDeviationSeconds))), point.cumulativeDeviationSeconds));
        return rows;
    }
    private static String signed(String value, double number) { return (number >= 0 ? "+" : "-") + value; }
    private void chooseCategories(DailyViewModel model) {
        if (current == null) return;
        String[] names = new String[current.categories.size()]; boolean[] selected = new boolean[names.length];
        for (int i = 0; i < names.length; i++) { CategoryEntity category = current.categories.get(i); names[i] = category.name; selected[i] = current.selectedCategoryIds.contains(category.id); }
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.daily_categories).setMultiChoiceItems(names, selected, null)
                .setNegativeButton(android.R.string.cancel, null).setPositiveButton(android.R.string.ok, null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Set<Long> ids = new HashSet<>(); android.widget.ListView choices = dialog.getListView();
            for (int i = 0; i < choices.getCount(); i++) if (choices.isItemChecked(i)) ids.add(current.categories.get(i).id);
            if (ids.isEmpty()) { Toast.makeText(requireContext(), R.string.daily_empty_selection, Toast.LENGTH_SHORT).show(); return; }
            model.setSelectedCategories(ids); dialog.dismiss();
        }));
        dialog.show();
    }
    private void chooseCustom(DailyViewModel model, Spinner period) {
        chooseDate(LocalDate.now(), from -> chooseDate(from, through -> {
            if (through.isBefore(from)) { Toast.makeText(requireContext(), R.string.invalid_custom_period, Toast.LENGTH_LONG).show(); return; }
            model.setCustom(from, through); period.setSelection(DailyViewModel.CUSTOM);
        }));
    }
    private void chooseDate(LocalDate initial, DateConsumer callback) {
        new DatePickerDialog(requireContext(), (picker, year, month, day) -> callback.accept(LocalDate.of(year, month + 1, day)),
                initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth()).show();
    }
    private interface DateConsumer { void accept(LocalDate value); }
}
