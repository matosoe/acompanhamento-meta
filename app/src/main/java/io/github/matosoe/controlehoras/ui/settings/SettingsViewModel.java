package io.github.matosoe.controlehoras.ui.settings;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.AppPreferenceEntity;
import io.github.matosoe.controlehoras.data.repository.AppPreferenceRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;

public final class SettingsViewModel extends AndroidViewModel {
    public static final String WEEK_START="week_start", DURATION_FORMAT="duration_format", CSV_SEPARATOR="csv_separator", THEME="theme";
    private final AppPreferenceRepository repository; private final MutableLiveData<Map<String,String>> values=new MutableLiveData<>(defaults());
    public SettingsViewModel(@NonNull Application app) { super(app); repository=new AppPreferenceRepository(AppDatabase.getInstance(app)); for(String key:defaults().keySet()) load(key); }
    @NonNull public LiveData<Map<String,String>> values() { return values; }
    public void put(@NonNull String key,@NonNull String value) { Map<String,String> copy=new HashMap<>(values.getValue()); copy.put(key,value); values.setValue(copy); repository.put(new AppPreferenceEntity(key,value),new RepositoryCallback<Void>() { public void onSuccess(Void ignored){} public void onError(Throwable error){} }); }
    private void load(String key) { repository.get(key,new RepositoryCallback<AppPreferenceEntity>() { public void onSuccess(@Nullable AppPreferenceEntity item) { if(item!=null) { Map<String,String> copy=new HashMap<>(values.getValue()); copy.put(item.key,item.value); values.postValue(copy); }} public void onError(Throwable error){} }); }
    private static Map<String,String> defaults() { Map<String,String> result=new HashMap<>(); result.put(WEEK_START,"Domingo"); result.put(DURATION_FORMAT,"HH:mm"); result.put(CSV_SEPARATOR,";"); result.put(THEME,"Sistema"); return result; }
}
