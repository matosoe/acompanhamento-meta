package io.github.matosoe.controlehoras.ui.today;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;

/** Collects activity fields and returns the raw values to TodayFragment for persistence. */
public final class TimeEntryFormDialogFragment extends DialogFragment {
    static final String RESULT_KEY = "time_entry_form";
    private static final String ARG_CATEGORY_IDS = "category_ids", ARG_CATEGORY_NAMES = "category_names";
    private static final String ARG_ID = "id", ARG_CATEGORY_ID = "category_id", ARG_START = "start", ARG_END = "end", ARG_NOTE = "note", ARG_CREATED = "created";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private long start, end;
    private Button startButton, endButton;
    private TextView durationView, validationView;

    static TimeEntryFormDialogFragment create(long[] categoryIds, String[] categoryNames, @Nullable TodayEntryItem entry, long defaultStart) {
        Bundle arguments = new Bundle();
        arguments.putLongArray(ARG_CATEGORY_IDS, categoryIds);
        arguments.putStringArray(ARG_CATEGORY_NAMES, categoryNames);
        if (entry != null) {
            arguments.putLong(ARG_ID, entry.id); arguments.putLong(ARG_CATEGORY_ID, entry.categoryId);
            arguments.putLong(ARG_START, entry.startEpochMillis); arguments.putLong(ARG_END, entry.endEpochMillis);
            arguments.putString(ARG_NOTE, entry.note); arguments.putLong(ARG_CREATED, entry.createdAtEpochMillis);
        } else { arguments.putLong(ARG_START, defaultStart); arguments.putLong(ARG_END, defaultStart + 3_600_000L); }
        TimeEntryFormDialogFragment fragment = new TimeEntryFormDialogFragment(); fragment.setArguments(arguments); return fragment;
    }

    @NonNull @Override public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        start = savedInstanceState == null ? args.getLong(ARG_START) : savedInstanceState.getLong(ARG_START);
        end = savedInstanceState == null ? args.getLong(ARG_END) : savedInstanceState.getLong(ARG_END);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_time_entry_form, null);
        Spinner category = view.findViewById(R.id.entry_category_input);
        String[] categoryNames = args.getStringArray(ARG_CATEGORY_NAMES);
        category.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames));
        long[] categoryIds = args.getLongArray(ARG_CATEGORY_IDS);
        long selectedCategory = args.getLong(ARG_CATEGORY_ID, categoryIds[0]);
        for (int i = 0; i < categoryIds.length; i++) if (categoryIds[i] == selectedCategory) { category.setSelection(i); break; }
        startButton = view.findViewById(R.id.entry_start_input); endButton = view.findViewById(R.id.entry_end_input);
        durationView = view.findViewById(R.id.entry_duration); validationView = view.findViewById(R.id.entry_validation);
        renderDateTimes();
        startButton.setOnClickListener(v -> chooseDateTime(true)); endButton.setOnClickListener(v -> chooseDateTime(false));
        EditText note = view.findViewById(R.id.entry_note_input); note.setText(args.getString(ARG_NOTE, ""));
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext()).setView(view)
                .setTitle(args.getLong(ARG_ID) == 0L ? R.string.add_activity : R.string.edit_activity)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.save, null);
        if (args.getLong(ARG_ID) != 0L) builder.setNeutralButton(R.string.delete, null);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(ignored -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (end <= start) { validationView.setText(R.string.invalid_time_range); return; }
            Bundle result = new Bundle();
            result.putLong(ARG_ID, args.getLong(ARG_ID)); result.putLong(ARG_CATEGORY_ID, categoryIds[category.getSelectedItemPosition()]);
            result.putLong(ARG_START, start); result.putLong(ARG_END, end); result.putString(ARG_NOTE, note.getText().toString().trim()); result.putLong(ARG_CREATED, args.getLong(ARG_CREATED));
            getParentFragmentManager().setFragmentResult(RESULT_KEY, result); dismiss();
        }));
        if (args.getLong(ARG_ID) != 0L) dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Bundle result = new Bundle(); result.putBoolean("delete", true); result.putLong(ARG_ID, args.getLong(ARG_ID));
            getParentFragmentManager().setFragmentResult(RESULT_KEY, result); dismiss();
        });
        return dialog;
    }

    private void chooseDateTime(boolean isStart) {
        LocalDateTime current = Instant.ofEpochMilli(isStart ? start : end).atZone(ZoneId.systemDefault()).toLocalDateTime();
        new DatePickerDialog(requireContext(), (picker, year, month, day) -> new TimePickerDialog(requireContext(), (timePicker, hour, minute) -> {
            long value = LocalDateTime.of(year, month + 1, day, hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (isStart) start = value; else end = value; renderDateTimes();
        }, current.getHour(), current.getMinute(), true).show(), current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth()).show();
    }

    private void renderDateTimes() {
        startButton.setText(Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).format(DATE_TIME));
        endButton.setText(Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).format(DATE_TIME));
        if (end > start) { durationView.setText(getString(R.string.duration_value, new DurationFormatter().hhMm((end - start) / 1_000L))); validationView.setText(""); }
        else { durationView.setText(""); validationView.setText(R.string.invalid_time_range); }
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_START, start);
        outState.putLong(ARG_END, end);
    }
}
