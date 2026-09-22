package io.github.matosoe.controlehoras.ui.today;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class TodayEntryDefaultsTest {
    @Test public void startsFirstEntryAtBeginningOfSelectedDay() {
        assertEquals(1_000L, TodayEntryDefaults.startAfterLastEntry(Collections.emptyList(), 1_000L));
    }

    @Test public void startsNewEntryAtLatestExistingEnd() {
        assertEquals(9_000L, TodayEntryDefaults.startAfterLastEntry(Arrays.asList(
                item(4_000L, 6_000L), item(7_000L, 9_000L), item(2_000L, 3_000L)), 1_000L));
    }

    private TodayEntryItem item(long start, long end) {
        return new TodayEntryItem(1L, 1L, start, end, "", 0L, "", "", "");
    }
}
