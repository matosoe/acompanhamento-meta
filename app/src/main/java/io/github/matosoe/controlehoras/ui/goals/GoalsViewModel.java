package io.github.matosoe.controlehoras.ui.goals;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.AppDatabase;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.data.repository.CategoryRepository;
import io.github.matosoe.controlehoras.data.repository.RepositoryCallback;
import io.github.matosoe.controlehoras.data.repository.TimeEntryRepository;
import io.github.matosoe.controlehoras.domain.model.GoalResult;
import io.github.matosoe.controlehoras.domain.service.DurationFormatter;
import io.github.matosoe.controlehoras.domain.service.GoalCalculator;
import io.github.matosoe.controlehoras.domain.service.PeriodAggregator;

public final class GoalsViewModel extends AndroidViewModel {
    public static final int DAY = 0, WEEK = 1, CUSTOM = 2;
    private final MutableLiveData<GoalsState> state = new MutableLiveData<>();
    private final CategoryRepository categories;
    private final TimeEntryRepository entries;
    private final ZoneId zone = ZoneId.systemDefault();
    private final GoalCalculator goalCalculator = new GoalCalculator();
    private final PeriodAggregator aggregator = new PeriodAggregator();
    private LocalDate start = LocalDate.now();
    private LocalDate end = start.plusDays(1);
    private int mode = DAY;

    public GoalsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        categories = new CategoryRepository(database);
        entries = new TimeEntryRepository(database);
        load();
    }

    @NonNull public LiveData<GoalsState> state() { return state; }
    public int mode() { return mode; }

    public void setMode(int value) {
        mode = value;
        LocalDate today = LocalDate.now();
        if (value == DAY) { start = today; end = today.plusDays(1); }
        else if (value == WEEK) { start = today.minusDays(today.getDayOfWeek().getValue() % 7); end = start.plusDays(7); }
        load();
    }

    public void setCustom(@NonNull LocalDate from, @NonNull LocalDate through) {
        if (through.isBefore(from)) throw new IllegalArgumentException("Período inválido.");
        mode = CUSTOM; start = from; end = through.plusDays(1); load();
    }

    public void reload() { load(); }
    public void update(@NonNull CategoryEntity category, @NonNull RepositoryCallback<Integer> callback) {
        categories.update(category, callback);
    }

    private void load() {
        LocalDate requestedStart = start, requestedEnd = end;
        state.setValue(new GoalsState(start, end, Collections.emptyList(), 0, 0, 0, true, ""));
        long periodStart = start.atStartOfDay(zone).toInstant().toEpochMilli();
        long periodEnd = end.atStartOfDay(zone).toInstant().toEpochMilli();
        categories.getAllOrdered(new RepositoryCallback<List<CategoryEntity>>() {
            @Override public void onSuccess(List<CategoryEntity> categoryList) {
                entries.getForPeriod(periodStart, periodEnd, null, new RepositoryCallback<List<TimeEntryEntity>>() {
                    @Override public void onSuccess(List<TimeEntryEntity> entryList) {
                        if (!requestedStart.equals(start) || !requestedEnd.equals(end)) return;
                        state.postValue(buildState(requestedStart, requestedEnd, periodStart, periodEnd,
                                categoryList, entryList));
                    }
                    @Override public void onError(Throwable error) { publishError(requestedStart, requestedEnd); }
                });
            }
            @Override public void onError(Throwable error) { publishError(requestedStart, requestedEnd); }
        });
    }

    @NonNull private GoalsState buildState(LocalDate requestedStart, LocalDate requestedEnd,
                                            long periodStart, long periodEnd,
                                            List<CategoryEntity> categoryList,
                                            List<TimeEntryEntity> entryList) {
        Map<Long, Long> actual = aggregator.byCategory(entryList, periodStart, periodEnd);
        long periodSeconds = (periodEnd - periodStart) / 1_000L;
        long registered = actual.values().stream().mapToLong(Long::longValue).sum();
        List<GoalRow> rows = new ArrayList<>();
        int targetTotal = 0;
        DurationFormatter formatter = new DurationFormatter();
        for (CategoryEntity category : categoryList) {
            GoalResult result = goalCalculator.calculate(actual.getOrDefault(category.id, 0L), periodSeconds,
                    category.targetBasisPoints, category.goalType);
            targetTotal += category.targetBasisPoints;
            double targetPercent = category.targetBasisPoints / 100.0;
            double deviationPoints = CategoryEntity.GOAL_TYPE_MAXIMUM.equals(category.goalType)
                    ? targetPercent - result.actualPercent : result.actualPercent - targetPercent;
            String detail = "Meta " + formatter.hhMm(Math.round(result.targetSeconds)) + " (" + percent(targetPercent)
                    + ") · Realizado " + formatter.hhMm(result.actualSeconds) + " (" + percent(result.actualPercent)
                    + ") · Desvio " + signed(formatter.hhMm(Math.round(Math.abs(result.deviationSeconds))), result.deviationSeconds)
                    + " / " + signed(percent(Math.abs(deviationPoints)), deviationPoints) + " · "
                    + (result.achieved ? "Atingida" : "Não atingida");
            rows.add(new GoalRow(category, detail));
        }
        return new GoalsState(requestedStart, requestedEnd, rows, registered,
                Math.max(0L, periodSeconds - registered), targetTotal, false, "");
    }

    private void publishError(LocalDate requestedStart, LocalDate requestedEnd) {
        if (requestedStart.equals(start) && requestedEnd.equals(end)) {
            state.postValue(new GoalsState(start, end, Collections.emptyList(), 0, 0, 0, false,
                    "Não foi possível carregar as metas."));
        }
    }

    private static String percent(double value) { return String.format(Locale.getDefault(), "%.1f%%", value); }
    private static String signed(String value, double number) { return (number >= 0 ? "+" : "-") + value; }
}
