package io.github.matosoe.controlehoras.ui.weekly;
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
import io.github.matosoe.controlehoras.domain.service.WeekCalendar;

public final class WeeklyViewModel extends AndroidViewModel {
    public static final int WEEKS_4 = 0, WEEKS_8 = 1, WEEKS_12 = 2, WEEKS_26 = 3;
    private static final int[] PERIODS = {4, 8, 12, 26};
    private final MutableLiveData<WeeklyState> state = new MutableLiveData<>();
    private final CategoryRepository categories; private final TimeEntryRepository entries;
    private final WeeklyCalculator calculator = new WeeklyCalculator(); private final WeekCalendar calendar = new WeekCalendar();
    private final ZoneId zone = ZoneId.systemDefault(); private final Set<Long> selectedIds = new HashSet<>();
    private int periodMode = WEEKS_4; private int selectedWeekIndex = -1;
    public WeeklyViewModel(@NonNull Application application) {
        super(application); AppDatabase db = AppDatabase.getInstance(application);
        categories = new CategoryRepository(db); entries = new TimeEntryRepository(db); load();
    }
    @NonNull public LiveData<WeeklyState> state() { return state; }
    public int periodMode() { return periodMode; }
    public void setPeriodMode(int mode) { periodMode = mode; selectedWeekIndex = -1; load(); }
    public void setSelectedCategories(@NonNull Set<Long> ids) { selectedIds.clear(); selectedIds.addAll(ids); load(); }
    public void selectWeek(int index) { WeeklyState current = state.getValue(); if (current != null && index >= 0 && index < current.chart.weeks.size()) {
        selectedWeekIndex = index; state.setValue(new WeeklyState(current.categories, new HashSet<>(selectedIds), current.chart,
                current.entries, index, false, current.errorMessage));
    }}
    private void load() {
        LocalDate end = LocalDate.now().plusDays(1); int weeks = PERIODS[periodMode];
        LocalDate first = calendar.sundayStart(end.minusDays(1)).minusDays((weeks - 1L) * 7L);
        categories.getAllOrdered(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> categoryList) {
                if (selectedIds.isEmpty()) for (CategoryEntity category : categoryList) selectedIds.add(category.id);
                long from = first.atStartOfDay(zone).toInstant().toEpochMilli(); long until = end.atStartOfDay(zone).toInstant().toEpochMilli();
                entries.getForPeriod(from, until, null, new RepositoryCallback<List<TimeEntryEntity>>() {
                    @Override public void onSuccess(List<TimeEntryEntity> itemList) {
                        WeeklyChartModel chart = calculator.calculate(categoryList, itemList, first, weeks, end, zone);
                        int chosen = selectedWeekIndex < 0 ? chart.weeks.size() - 1 : Math.min(selectedWeekIndex, chart.weeks.size() - 1);
                        state.postValue(new WeeklyState(categoryList, new HashSet<>(selectedIds), chart, itemList, chosen, false, ""));
                    }
                    @Override public void onError(Throwable error) { publishError(); }
                });
            }
            @Override public void onError(Throwable error) { publishError(); }
        });
    }
    private void publishError() { state.postValue(new WeeklyState(Collections.emptyList(), new HashSet<>(selectedIds),
            new WeeklyChartModel(Collections.emptyList()), Collections.emptyList(), -1, false,
            "Não foi possível carregar o acompanhamento semanal.")); }
}
