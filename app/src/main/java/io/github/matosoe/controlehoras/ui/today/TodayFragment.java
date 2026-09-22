package io.github.matosoe.controlehoras.ui.today;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import java.util.List;
import java.util.Collections;
import java.lang.StringBuilder;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;

public class TodayFragment extends Fragment {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<TodayEntryItem> displayedEntries = Collections.emptyList();
    public TodayFragment() { super(R.layout.fragment_today); }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = view.findViewById(R.id.today_title);
        TextView summary = view.findViewById(R.id.today_summary);
        TextView empty = view.findViewById(R.id.today_empty);
        TextView alert = view.findViewById(R.id.today_alert);
        TextView categorySummary = view.findViewById(R.id.today_category_summary);
        TodayViewModel model = new ViewModelProvider(this).get(TodayViewModel.class);
        TodayEntryAdapter adapter = new TodayEntryAdapter(item -> openForm(model, item));
        RecyclerView entries = view.findViewById(R.id.today_entries);
        entries.setLayoutManager(new LinearLayoutManager(requireContext()));
        entries.setAdapter(adapter);
        view.findViewById(R.id.previous_day).setOnClickListener(v -> model.showPreviousDay());
        view.findViewById(R.id.next_day).setOnClickListener(v -> model.showNextDay());
        view.findViewById(R.id.today_add).setOnClickListener(v -> openForm(model, null));
        getParentFragmentManager().setFragmentResultListener(TimeEntryFormDialogFragment.RESULT_KEY, getViewLifecycleOwner(), (key, result) -> {
            if (result.getBoolean("delete")) { confirmDelete(model, result.getLong("id")); return; }
            TimeEntryEntity entry = new TimeEntryEntity(result.getLong("id"), result.getLong("category_id"),
                    result.getLong("start"), result.getLong("end"), 0L, result.getString("note", ""), result.getLong("created"), 0L);
            model.save(entry, new RepositoryCallback<Long>() {
                @Override public void onSuccess(Long value) { requireActivity().runOnUiThread(model::refresh); }
                @Override public void onError(Throwable error) { requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show()); }
            });
        });
        model.getState().observe(getViewLifecycleOwner(), state -> {
            title.setText(state.selectedDate.format(DATE_FORMAT));
            if (state.loading) { summary.setText(R.string.today_loading); empty.setVisibility(View.GONE); entries.setVisibility(View.GONE); return; }
            if (!state.errorMessage.isEmpty()) { summary.setText(state.errorMessage); empty.setVisibility(View.GONE); entries.setVisibility(View.GONE); alert.setText(""); categorySummary.setText(""); return; }
            displayedEntries = state.entries;
            adapter.submitList(state.entries);
            TodaySummary daily = state.summary;
            DurationFormatter formatter = new DurationFormatter();
            summary.setText(getString(R.string.registered_total, formatter.hhMm(daily.registeredSeconds), formatter.hhMm(daily.unclassifiedSeconds)));
            StringBuilder categories = new StringBuilder(); for (TodaySummary.CategoryTotal total : daily.categories) { if (categories.length() > 0) categories.append(" · "); categories.append(total.name).append(": ").append(formatter.hhMm(total.seconds)); }
            categorySummary.setText(categories);
            StringBuilder warnings = new StringBuilder();
            if (daily.unclassifiedSeconds > 0) warnings.append(getString(R.string.gap_warning));
            if (daily.hasOverlap) { if (warnings.length() > 0) warnings.append(" "); warnings.append(getString(R.string.overlap_warning)); }
            if (daily.exceedsDay) { if (warnings.length() > 0) warnings.append(" "); warnings.append(getString(R.string.exceeds_day_warning)); }
            alert.setText(warnings);
            boolean isEmpty = state.entries.isEmpty();
            empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            entries.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });
    }

    private void openForm(TodayViewModel model, @Nullable TodayEntryItem entry) {
        final long defaultStart = entry == null
                ? TodayEntryDefaults.startAfterLastEntry(displayedEntries, model.getSelectedDayStartMillis())
                : model.getSelectedDayStartMillis();
        model.getCategories(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> categories) {
                long[] ids = new long[categories.size()]; String[] names = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) { ids[i] = categories.get(i).id; names[i] = categories.get(i).name; }
                requireActivity().runOnUiThread(() -> TimeEntryFormDialogFragment.create(ids, names, entry, defaultStart).show(getParentFragmentManager(), "time_entry_form"));
            }
            @Override public void onError(Throwable error) { requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show()); }
        });
    }

    private void confirmDelete(TodayViewModel model, long id) {
        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.confirm_delete).setMessage(R.string.delete_confirmation)
                .setNegativeButton(android.R.string.cancel, null).setPositiveButton(R.string.delete, (dialog, which) -> {
                    TimeEntryEntity entry = new TimeEntryEntity(id, 0L, 0L, 0L, 0L, null, 0L, 0L);
                    model.delete(entry, new RepositoryCallback<Integer>() {
                        @Override public void onSuccess(Integer value) { requireActivity().runOnUiThread(model::refresh); }
                        @Override public void onError(Throwable error) { requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show()); }
                    });
                }).show();
    }
}
