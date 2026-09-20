package io.github.matosoe.controlehoras.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;

public final class PeriodAggregator {
 public Map<Long,Long> byCategory(List<TimeEntryEntity> entries,long start,long end) {
  Map<Long,Long> totals=new HashMap<>(); for(TimeEntryEntity e:entries){long a=Math.max(start,e.startEpochMillis),b=Math.min(end,e.endEpochMillis);if(b>a)totals.merge(e.categoryId,(b-a)/1000L,Long::sum);} return totals;
 }
 public long unclassifiedSeconds(List<TimeEntryEntity> entries,long start,long end) { long used=byCategory(entries,start,end).values().stream().mapToLong(Long::longValue).sum(); return Math.max(0,(end-start)/1000L-used); }
}
