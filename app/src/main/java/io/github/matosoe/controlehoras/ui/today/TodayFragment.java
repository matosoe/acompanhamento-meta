package io.github.matosoe.controlehoras.ui.today;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import io.github.matosoe.controlehoras.R;

public class TodayFragment extends Fragment {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public TodayFragment() { super(R.layout.fragment_today); }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = view.findViewById(R.id.today_title);
        TextView summary = view.findViewById(R.id.today_summary);
        TextView empty = view.findViewById(R.id.today_empty);
        TodayEntryAdapter adapter = new TodayEntryAdapter();
        RecyclerView entries = view.findViewById(R.id.today_entries);
        entries.setLayoutManager(new LinearLayoutManager(requireContext()));
        entries.setAdapter(adapter);
        TodayViewModel model = new ViewModelProvider(this).get(TodayViewModel.class);
        view.findViewById(R.id.previous_day).setOnClickListener(v -> model.showPreviousDay());
        view.findViewById(R.id.next_day).setOnClickListener(v -> model.showNextDay());
        model.getState().observe(getViewLifecycleOwner(), state -> {
            title.setText(state.selectedDate.format(DATE_FORMAT));
            if (state.loading) { summary.setText(R.string.today_loading); empty.setVisibility(View.GONE); entries.setVisibility(View.GONE); return; }
            if (!state.errorMessage.isEmpty()) { summary.setText(state.errorMessage); empty.setVisibility(View.GONE); entries.setVisibility(View.GONE); return; }
            adapter.submitList(state.entries);
            summary.setText(getString(R.string.today_entry_count, state.entries.size()));
            boolean isEmpty = state.entries.isEmpty();
            empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            entries.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });
    }
}
