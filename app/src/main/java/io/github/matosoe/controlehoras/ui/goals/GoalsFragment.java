package io.github.matosoe.controlehoras.ui.goals;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;

public final class GoalsFragment extends Fragment {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public GoalsFragment() { super(R.layout.fragment_goals); }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        GoalsViewModel model = new ViewModelProvider(this).get(GoalsViewModel.class);
        Spinner period = view.findViewById(R.id.goals_period);
        period.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.goal_periods)));
        period.setSelection(model.mode());
        GoalsAdapter adapter = new GoalsAdapter(row -> editGoal(model, row));
        RecyclerView list = view.findViewById(R.id.goals_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
        period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) { }
            @Override public void onItemSelected(AdapterView<?> parent, View selected, int position, long id) {
                if (position != model.mode()) model.setMode(position);
            }
        });
        view.findViewById(R.id.goals_custom).setOnClickListener(v -> chooseCustomPeriod(model, period));
        TextView range = view.findViewById(R.id.goals_range);
        TextView total = view.findViewById(R.id.goals_total);
        TextView warning = view.findViewById(R.id.goals_warning);
        TextView error = view.findViewById(R.id.goals_error);
        model.state().observe(getViewLifecycleOwner(), screen -> {
            if (screen.loading) return;
            error.setText(screen.errorMessage);
            adapter.submit(screen.rows);
            range.setText(getString(R.string.goals_range, screen.start.format(DATE_FORMAT),
                    screen.end.minusDays(1).format(DATE_FORMAT)));
            DurationFormatter formatter = new DurationFormatter();
            total.setText(getString(R.string.goals_totals, formatter.hhMm(screen.registered),
                    formatter.hhMm(screen.unclassified)));
            warning.setText(screen.targetTotal == 10_000 ? "" :
                    getString(R.string.goals_target_warning, screen.targetTotal / 100.0));
        });
    }

    private void chooseCustomPeriod(GoalsViewModel model, Spinner period) {
        LocalDate initial = LocalDate.now();
        chooseDate(initial, start -> chooseDate(start, end -> {
            if (end.isBefore(start)) {
                Toast.makeText(requireContext(), R.string.invalid_custom_period, Toast.LENGTH_LONG).show();
                return;
            }
            model.setCustom(start, end);
            period.setSelection(GoalsViewModel.CUSTOM);
        }));
    }

    private void chooseDate(LocalDate initial, DateConsumer consumer) {
        new DatePickerDialog(requireContext(), (picker, year, month, day) ->
                consumer.accept(LocalDate.of(year, month + 1, day)), initial.getYear(),
                initial.getMonthValue() - 1, initial.getDayOfMonth()).show();
    }

    private void editGoal(GoalsViewModel model, GoalRow row) {
        View content = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_goal, null);
        EditText target = content.findViewById(R.id.goal_target_input);
        target.setText(String.format(Locale.getDefault(), "%.2f", row.category.targetBasisPoints / 100.0));
        Spinner type = content.findViewById(R.id.goal_type_input);
        String[] types = {CategoryEntity.GOAL_TYPE_MINIMUM, CategoryEntity.GOAL_TYPE_MAXIMUM};
        type.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, types));
        type.setSelection(CategoryEntity.GOAL_TYPE_MAXIMUM.equals(row.category.goalType) ? 1 : 0);
        TextView error = content.findViewById(R.id.goal_edit_error);
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext()).setTitle(row.category.name)
                .setView(content).setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.save, null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int basisPoints;
            try {
                basisPoints = (int) Math.round(Double.parseDouble(target.getText().toString().replace(',', '.')) * 100);
            } catch (NumberFormatException exception) {
                error.setText(R.string.invalid_target);
                return;
            }
            if (basisPoints < 0 || basisPoints > 10_000) {
                error.setText(R.string.invalid_target);
                return;
            }
            CategoryEntity updated = new CategoryEntity(row.category.id, row.category.name,
                    row.category.colorArgb, row.category.displayOrder, types[type.getSelectedItemPosition()],
                    basisPoints, row.category.active);
            model.update(updated, new RepositoryCallback<Integer>() {
                @Override public void onSuccess(Integer changed) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> { dialog.dismiss(); model.reload(); });
                }
                @Override public void onError(Throwable failure) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> error.setText(R.string.goals_save_error));
                }
            });
        }));
        dialog.show();
    }

    private interface DateConsumer { void accept(LocalDate value); }
}
