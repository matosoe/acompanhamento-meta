package io.github.matosoe.controlehoras.ui.export;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.matosoe.controlehoras.BuildConfig;
import io.github.matosoe.controlehoras.R;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;
import io.github.matosoe.controlehoras.data.repository.AppPreferenceRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.domain.service.CsvExporter;

public final class ExportFragment extends Fragment {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ZoneId zone = ZoneId.systemDefault();
    private ExportState current = ExportState.empty(); private ExportViewModel model; private TextView filters, summary, error; private char separator = ';';
    private ActivityResultLauncher<String> createDocument;
    public ExportFragment() { super(R.layout.fragment_export); }
    @Override public void onCreate(@Nullable Bundle saved) { super.onCreate(saved); createDocument=registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), this::saveToUri); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view,saved); model=new ViewModelProvider(this).get(ExportViewModel.class); filters=view.findViewById(R.id.export_filters); summary=view.findViewById(R.id.export_summary); error=view.findViewById(R.id.export_error);
        new AppPreferenceRepository(AppDatabase.getInstance(requireContext())).get("csv_separator", new RepositoryCallback<AppPreferenceEntity>() { public void onSuccess(AppPreferenceEntity preference) { if(preference != null && preference.value.length() == 1) separator=preference.value.charAt(0); } public void onError(Throwable ignored) {} });
        ExportEntryAdapter adapter=new ExportEntryAdapter(); androidx.recyclerview.widget.RecyclerView list=view.findViewById(R.id.export_list); list.setLayoutManager(new LinearLayoutManager(requireContext())); list.setAdapter(adapter);
        ((android.widget.EditText)view.findViewById(R.id.export_search)).addTextChangedListener(new android.text.TextWatcher() { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){model.setQuery(s.toString());} public void afterTextChanged(android.text.Editable s){} });
        view.findViewById(R.id.export_dates).setOnClickListener(v -> chooseDates());
        view.findViewById(R.id.export_save).setOnClickListener(v -> { if(current.entries.isEmpty()) empty(); else createDocument.launch(fileName()); });
        view.findViewById(R.id.export_share).setOnClickListener(v -> share());
        ((TextView)view.findViewById(R.id.export_version)).setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        model.state().observe(getViewLifecycleOwner(), state -> { current=state; configureCategory(view, state.categories); adapter.submit(state.entries); summary.setText(getResources().getQuantityString(R.plurals.export_summary, state.entries.size(), state.entries.size())); error.setText(state.error); filters.setText(filterText()); });
    }
    private void configureCategory(View root, List<CategoryEntity> categories) {
        AutoCompleteTextView input=root.findViewById(R.id.export_category); List<String> labels=new ArrayList<>(); labels.add(getString(R.string.export_all_categories)); for(CategoryEntity category:categories) labels.add(category.name);
        input.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, labels));
        input.setOnItemClickListener((parent, view, position, id) -> model.setCategory(position == 0 ? null : categories.get(position-1).id));
        if(input.getText().length()==0) input.setText(labels.get(0), false);
    }
    private void chooseDates() {
        MaterialDatePicker<androidx.core.util.Pair<Long,Long>> picker=MaterialDatePicker.Builder.dateRangePicker().setTitleText(R.string.export_dates).build();
        picker.addOnPositiveButtonClickListener(pair -> { LocalDate from=toDate(pair.first), until=toDate(pair.second); if(until.isBefore(from)) { Toast.makeText(requireContext(),R.string.invalid_custom_period,Toast.LENGTH_SHORT).show(); return; } model.setDates(from,until); }); picker.show(getParentFragmentManager(),"export_dates");
    }
    private LocalDate toDate(Long utcMillis) { return Instant.ofEpochMilli(utcMillis).atZone(java.time.ZoneOffset.UTC).toLocalDate(); }
    private String filterText() { String value=model.from()==null ? getString(R.string.export_all_dates) : model.from().format(DATE)+" – "+model.untilInclusive().format(DATE); return getString(R.string.export_filter_description,value); }
    private String fileName() { return "controle_horas_"+new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.ROOT).format(Calendar.getInstance().getTime())+".csv"; }
    private byte[] bytes() { List<CsvExporter.CsvRow> rows=new ArrayList<>(); CsvExporter exporter=new CsvExporter(); for(ExportEntryItem item:current.entries) rows.add(exporter.row(item.entry,item.categoryName,zone)); return exporter.export(rows, separator); }
    private void saveToUri(@Nullable Uri uri) { if(uri==null) return; try(OutputStream out=requireContext().getContentResolver().openOutputStream(uri,"w")) { if(out==null) throw new java.io.IOException("Destino indisponível"); out.write(bytes()); Toast.makeText(requireContext(),getResources().getQuantityString(R.plurals.export_saved,current.entries.size(),current.entries.size()),Toast.LENGTH_LONG).show(); } catch(Exception error) { Toast.makeText(requireContext(),R.string.export_failed,Toast.LENGTH_LONG).show(); } }
    private void share() { if(current.entries.isEmpty()) { empty(); return; } try { File dir=new File(requireContext().getCacheDir(),"exports"); if(!dir.exists() && !dir.mkdirs()) throw new java.io.IOException("Cache indisponível"); File file=new File(dir,fileName()); try(OutputStream out=new FileOutputStream(file)) { out.write(bytes()); } Uri uri=FileProvider.getUriForFile(requireContext(),requireContext().getPackageName()+".fileprovider",file); Intent intent=new Intent(Intent.ACTION_SEND).setType("text/csv").putExtra(Intent.EXTRA_STREAM,uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); startActivity(Intent.createChooser(intent,getString(R.string.export_share))); } catch(Exception error) { Toast.makeText(requireContext(),R.string.export_failed,Toast.LENGTH_LONG).show(); } }
    private void empty() { Toast.makeText(requireContext(),R.string.export_empty,Toast.LENGTH_SHORT).show(); }
}
