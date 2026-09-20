package io.github.matosoe.controlehoras.ui.today;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.CategoryRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.data.repository.TimeEntryRepository;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;

public final class TodayViewModel extends AndroidViewModel {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final MutableLiveData<TodayUiState> state = new MutableLiveData<>();
    private final TimeEntryRepository timeEntries;
    private final CategoryRepository categories;
    private final ZoneId zoneId = ZoneId.systemDefault();
    private final DurationFormatter durationFormatter = new DurationFormatter();
    private final TodaySummaryCalculator summaryCalculator = new TodaySummaryCalculator();
    private LocalDate selectedDate = LocalDate.now();

    public TodayViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        timeEntries = new TimeEntryRepository(database);
        categories = new CategoryRepository(database);
        loadSelectedDay();
    }

    @NonNull public LiveData<TodayUiState> getState() { return state; }
    public void showPreviousDay() { selectedDate = selectedDate.minusDays(1); loadSelectedDay(); }
    public void showNextDay() { selectedDate = selectedDate.plusDays(1); loadSelectedDay(); }
    public void getCategories(@NonNull RepositoryCallback<List<CategoryEntity>> callback) { categories.getAllOrdered(callback); }
    public void save(@NonNull TimeEntryEntity draft, @NonNull RepositoryCallback<Long> callback) { timeEntries.save(draft, callback); }
    public void delete(@NonNull TimeEntryEntity entry, @NonNull RepositoryCallback<Integer> callback) { timeEntries.delete(entry, callback); }
    public void refresh() { loadSelectedDay(); }
    public long getSelectedDayStartMillis() { return selectedDate.atStartOfDay(zoneId).toInstant().toEpochMilli(); }

    private void loadSelectedDay() {
        final LocalDate requestedDate = selectedDate;
        state.setValue(TodayUiState.loading(requestedDate));
        long start = requestedDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
        long end = requestedDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
        categories.getAllOrdered(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> categoryResult) {
                Map<Long, String> names = new HashMap<>();
                for (CategoryEntity category : categoryResult) names.put(category.id, category.name);
                timeEntries.getForPeriod(start, end, null, new RepositoryCallback<List<TimeEntryEntity>>() {
                    @Override public void onSuccess(List<TimeEntryEntity> entries) {
                        if (!requestedDate.equals(selectedDate)) return;
                        state.postValue(new TodayUiState(requestedDate, mapEntries(entries, names), false, "",
                                summaryCalculator.calculate(entries, start, end, names)));
                    }

                    @Override public void onError(Throwable error) { publishError(requestedDate); }
                });
            }

            @Override public void onError(Throwable error) { publishError(requestedDate); }
        });
    }

    private void publishError(@NonNull LocalDate requestedDate) {
        if (requestedDate.equals(selectedDate)) {
            state.postValue(new TodayUiState(requestedDate, new ArrayList<>(), false,
                    "Não foi possível carregar os lançamentos.", null));
        }
    }

    @NonNull private List<TodayEntryItem> mapEntries(@NonNull List<TimeEntryEntity> entries, @NonNull Map<Long, String> categoryNames) {
        List<TodayEntryItem> items = new ArrayList<>();
        for (TimeEntryEntity entry : entries) {
            String start = Instant.ofEpochMilli(entry.startEpochMillis).atZone(zoneId).format(TIME_FORMAT);
            String end = Instant.ofEpochMilli(entry.endEpochMillis).atZone(zoneId).format(TIME_FORMAT);
            String category = categoryNames.get(entry.categoryId);
            items.add(new TodayEntryItem(entry.id, entry.categoryId, entry.startEpochMillis, entry.endEpochMillis,
                    entry.note == null ? "" : entry.note, entry.createdAtEpochMillis, start + "–" + end,
                    category == null ? "Categoria indisponível" : category, durationFormatter.hhMm(entry.durationSeconds)));
        }
        return items;
    }
}
