package io.github.matosoe.controlehoras.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.Map;
import io.github.matosoe.controlehoras.BuildConfig;
import io.github.matosoe.controlehoras.R;

public final class SettingsFragment extends Fragment {
    private boolean binding;
    public SettingsFragment() { super(R.layout.fragment_settings); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view,saved); SettingsViewModel model=new ViewModelProvider(this).get(SettingsViewModel.class);
        AutoCompleteTextView week=view.findViewById(R.id.settings_week_start), duration=view.findViewById(R.id.settings_duration), separator=view.findViewById(R.id.settings_separator), theme=view.findViewById(R.id.settings_theme);
        setup(week,R.array.settings_week_start_values,value->model.put(SettingsViewModel.WEEK_START,value));
        setup(duration,R.array.settings_duration_values,value->model.put(SettingsViewModel.DURATION_FORMAT,value));
        setup(separator,R.array.settings_separator_values,value->model.put(SettingsViewModel.CSV_SEPARATOR,value));
        setup(theme,R.array.settings_theme_values,value->{ model.put(SettingsViewModel.THEME,value); applyTheme(value); });
        ((TextView)view.findViewById(R.id.settings_version)).setText(getString(R.string.settings_version,BuildConfig.VERSION_NAME));
        model.values().observe(getViewLifecycleOwner(), values -> { binding=true; select(week,values.get(SettingsViewModel.WEEK_START)); select(duration,values.get(SettingsViewModel.DURATION_FORMAT)); select(separator,values.get(SettingsViewModel.CSV_SEPARATOR)); select(theme,values.get(SettingsViewModel.THEME)); binding=false; });
    }
    private void setup(AutoCompleteTextView view,int array,ValueListener listener) { String[] values=getResources().getStringArray(array); view.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1,values)); view.setOnItemClickListener((p,v,pos,id)-> { if(!binding) listener.set(values[pos]); }); }
    private static void select(AutoCompleteTextView view,String value) { view.setText(value,false); }
    private static void applyTheme(String theme) { AppCompatDelegate.setDefaultNightMode("Claro".equals(theme) ? AppCompatDelegate.MODE_NIGHT_NO : "Escuro".equals(theme) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); }
    private interface ValueListener { void set(String value); }
}
