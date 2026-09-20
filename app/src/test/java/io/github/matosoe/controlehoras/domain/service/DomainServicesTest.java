package io.github.matosoe.controlehoras.domain.service;

import static org.junit.Assert.*;
import java.time.*;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;
import io.github.matosoe.controlehoras.data.local.entity.TimeEntryEntity;
import io.github.matosoe.controlehoras.domain.model.GoalResult;

public class DomainServicesTest {
 @Test public void splitterPreservesSecondsAcrossMidnight() {
  ZoneId z=ZoneId.of("America/Sao_Paulo"); long a=ZonedDateTime.of(2026,9,6,23,30,0,0,z).toInstant().toEpochMilli(), b=ZonedDateTime.of(2026,9,7,1,0,0,0,z).toInstant().toEpochMilli();
  assertEquals(2,new PeriodSplitter().split(a,b,z).size()); assertEquals(5400,new PeriodSplitter().split(a,b,z).stream().mapToLong(s->s.durationSeconds).sum());
 }
 @Test public void splitterRejectsEmptyAndUsesLocalDayBoundariesAcrossDst() {
  ZoneId z=ZoneId.of("America/New_York"); long start=ZonedDateTime.of(2026,3,8,0,30,0,0,z).toInstant().toEpochMilli(), end=ZonedDateTime.of(2026,3,9,0,30,0,0,z).toInstant().toEpochMilli();
  assertEquals(2,new PeriodSplitter().split(start,end,z).size());
  assertEquals(23 * 3600L,new PeriodSplitter().split(start,end,z).stream().mapToLong(s->s.durationSeconds).sum());
  try { new PeriodSplitter().split(start,start,z); fail("Expected invalid interval"); } catch (IllegalArgumentException expected) { assertTrue(expected.getMessage().contains("posterior")); }
 }
 @Test public void goalsHonorMinimumAndMaximum() {
  GoalCalculator c=new GoalCalculator(); assertTrue(c.calculate(400,1000,3000,CategoryEntity.GOAL_TYPE_MINIMUM).achieved); assertTrue(c.calculate(200,1000,3000,CategoryEntity.GOAL_TYPE_MAXIMUM).achieved); assertFalse(c.calculate(400,1000,3000,CategoryEntity.GOAL_TYPE_MAXIMUM).achieved);
 }
 @Test public void weekIdMatchesAdrEpoch() { assertEquals(253,new WeekCalendar().sequentialWeek(LocalDate.of(2026,9,6))); }
 @Test public void weekCalendarSupportsDatesBeforeEpochAndDisplayPreference() {
  WeekCalendar calendar=new WeekCalendar(); assertEquals(0,calendar.sequentialWeek(LocalDate.of(2021,10,31)));
  assertEquals(DayOfWeek.SUNDAY,calendar.displayStart(false)); assertEquals(DayOfWeek.MONDAY,calendar.displayStart(true));
 }
 @Test public void formatsAreStable() { DurationFormatter f=new DurationFormatter(); assertEquals("01:02",f.hhMm(3750)); assertEquals("01:02:30",f.csv(3750)); assertEquals("1.04",f.decimalHours(3750)); }
 @Test public void aggregatorClipsPeriodAndExcludesOutsideEntries() {
  TimeEntryEntity before=new TimeEntryEntity(1,1,-10_000,10_000,20,"",0,0), inside=new TimeEntryEntity(2,2,30_000,50_000,20,"",0,0), after=new TimeEntryEntity(3,3,60_000,70_000,10,"",0,0);
  PeriodAggregator aggregator=new PeriodAggregator(); Map<Long,Long> values=aggregator.byCategory(Arrays.asList(before,inside,after),0,60_000);
  assertEquals(Long.valueOf(10),values.get(1L)); assertEquals(Long.valueOf(20),values.get(2L)); assertFalse(values.containsKey(3L)); assertEquals(30,aggregator.unclassifiedSeconds(Arrays.asList(before,inside,after),0,60_000));
 }
 @Test public void goalsHandleZeroPeriodAndRejectInvalidValues() {
  GoalResult zero=new GoalCalculator().calculate(0,0,0,CategoryEntity.GOAL_TYPE_MINIMUM); assertEquals(0d,zero.actualPercent,0d); assertTrue(zero.achieved);
  try { new GoalCalculator().calculate(0,-1,0,CategoryEntity.GOAL_TYPE_MINIMUM); fail("Expected invalid period"); } catch (IllegalArgumentException expected) { }
  try { new GoalCalculator().calculate(0,1,-1,CategoryEntity.GOAL_TYPE_MINIMUM); fail("Expected invalid target"); } catch (IllegalArgumentException expected) { }
 }
}
