package io.github.matosoe.controlehoras.ui.daily;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.CategoryRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.data.repository.TimeEntryRepository;

public final class DailyViewModel extends AndroidViewModel {
    public static final int DAYS_7 = 0, DAYS_30 = 1, DAYS_90 = 2, CUSTOM = 3;
    private final MutableLiveData<DailyState> state = new MutableLiveData<>();
    private final CategoryRepository categories;
    private final TimeEntryRepository entries;
    private final DailyChartCalculator calculator = new DailyChartCalculator();
    private final ZoneId zone = ZoneId.systemDefault();
    private LocalDate start = LocalDate.now().minusDays(29);
    private LocalDate endExclusive = LocalDate.now().plusDays(1);
    private int periodMode = DAYS_30;
    private final Set<Long> selectedIds = new HashSet<>();

    public DailyViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        categories = new CategoryRepository(database);
        entries = new TimeEntryRepository(database);
        load();
    }
    @NonNull public LiveData<DailyState> state() { return state; }
    public int periodMode() { return periodMode; }
    public void setPeriodMode(int mode) {
        periodMode = mode;
        LocalDate today = LocalDate.now();
        if (mode == DAYS_7) { start = today.minusDays(6); endExclusive = today.plusDays(1); }
        else if (mode == DAYS_30) { start = today.minusDays(29); endExclusive = today.plusDays(1); }
        else if (mode == DAYS_90) { start = today.minusDays(89); endExclusive = today.plusDays(1); }
        load();
    }
    public void setCustom(@NonNull LocalDate from, @NonNull LocalDate through) {
        if (through.isBefore(from)) throw new IllegalArgumentException("Período inválido.");
        periodMode = CUSTOM; start = from; endExclusive = through.plusDays(1); load();
    }
    public void setSelectedCategories(@NonNull Set<Long> ids) { selectedIds.clear(); selectedIds.addAll(ids); load(); }
    private void load() {
        LocalDate requestedStart = start, requestedEnd = endExclusive;
        categories.getAllOrdered(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> list) {
                if (selectedIds.isEmpty()) for (CategoryEntity category : list) selectedIds.add(category.id);
                long from = requestedStart.atStartOfDay(zone).toInstant().toEpochMilli();
                long until = requestedEnd.atStartOfDay(zone).toInstant().toEpochMilli();
                entries.getForPeriod(from, until, null, new RepositoryCallback<List<TimeEntryEntity>>() {
                    @Override public void onSuccess(List<TimeEntryEntity> entryList) {
                        if (!requestedStart.equals(start) || !requestedEnd.equals(endExclusive)) return;
                        state.postValue(new DailyState(requestedStart, requestedEnd, list, new HashSet<>(selectedIds),
                                calculator.calculate(list, entryList, requestedStart, requestedEnd, selectedIds, zone), false, ""));
                    }
                    @Override public void onError(Throwable error) { error(requestedStart, requestedEnd); }
                });
            }
            @Override public void onError(Throwable failure) { error(requestedStart, requestedEnd); }
        });
    }
    private void error(LocalDate requestedStart, LocalDate requestedEnd) {
        if (requestedStart.equals(start) && requestedEnd.equals(endExclusive)) state.postValue(
                new DailyState(start, endExclusive, Collections.emptyList(), new HashSet<>(selectedIds),
                        new DailyChartModel(start, endExclusive, Collections.emptyList()), false,
                        "Não foi possível carregar a evolução diária."));
    }
}
