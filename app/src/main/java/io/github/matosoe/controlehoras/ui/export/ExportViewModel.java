package io.github.matosoe.controlehoras.ui.export;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.CategoryRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.data.repository.TimeEntryRepository;

public final class ExportViewModel extends AndroidViewModel {
    private final MutableLiveData<ExportState> state = new MutableLiveData<>(ExportState.empty());
    private final CategoryRepository categories; private final TimeEntryRepository entries; private final ZoneId zone = ZoneId.systemDefault();
    @Nullable private LocalDate from, untilInclusive; @Nullable private Long categoryId; @NonNull private String query = ""; private ExportState unfiltered = ExportState.empty();
    public ExportViewModel(@NonNull Application app) { super(app); AppDatabase db=AppDatabase.getInstance(app); categories=new CategoryRepository(db); entries=new TimeEntryRepository(db); load(); }
    @NonNull public LiveData<ExportState> state() { return state; }
    public void setDates(@Nullable LocalDate from, @Nullable LocalDate untilInclusive) { this.from=from; this.untilInclusive=untilInclusive; load(); }
    public void setCategory(@Nullable Long id) { categoryId=id; load(); }
    public void setQuery(@NonNull String value) { query=value.toLowerCase(Locale.ROOT).trim(); publishFiltered(unfiltered); }
    @Nullable public LocalDate from() { return from; } @Nullable public LocalDate untilInclusive() { return untilInclusive; }
    private void load() {
        Long start=from == null ? null : from.atStartOfDay(zone).toInstant().toEpochMilli();
        Long end=untilInclusive == null ? null : untilInclusive.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
        categories.getAllOrdered(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> list) { entries.search(start, end, categoryId, new RepositoryCallback<List<TimeEntryEntity>>() {
                @Override public void onSuccess(List<TimeEntryEntity> values) { Map<Long,String> names=new HashMap<>(); for(CategoryEntity c:list) names.put(c.id,c.name); List<ExportEntryItem> result=new ArrayList<>(); for(TimeEntryEntity e:values) result.add(new ExportEntryItem(e, names.getOrDefault(e.categoryId, "Categoria removida"))); publish(new ExportState(list,result,"")); }
                @Override public void onError(Throwable error) { publish(new ExportState(list,new ArrayList<>(),"Não foi possível carregar os registros.")); }
            }); }
            @Override public void onError(Throwable error) { publish(new ExportState(new ArrayList<>(),new ArrayList<>(),"Não foi possível carregar as categorias.")); }
        });
    }
    private void publish(@NonNull ExportState value) { unfiltered=value; state.postValue(filter(value)); }
    private void publishFiltered(@Nullable ExportState current) { if(current != null) state.setValue(filter(current)); }
    private ExportState filter(ExportState value) { if(query.isEmpty()) return value; List<ExportEntryItem> result=new ArrayList<>(); for(ExportEntryItem item:value.entries) if(item.searchable.contains(query)) result.add(item); return new ExportState(value.categories,result,value.error); }
}
