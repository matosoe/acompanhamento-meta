package io.github.matosoe.controlehoras.domain.service;

import static org.junit.Assert.*;
import java.time.*;
import org.junit.Test;
import io.github.matosoe.controlehoras.data.local.entity.CategoryEntity;

public class DomainServicesTest {
 @Test public void splitterPreservesSecondsAcrossMidnight() {
  ZoneId z=ZoneId.of("America/Sao_Paulo"); long a=ZonedDateTime.of(2026,9,6,23,30,0,0,z).toInstant().toEpochMilli(), b=ZonedDateTime.of(2026,9,7,1,0,0,0,z).toInstant().toEpochMilli();
  assertEquals(2,new PeriodSplitter().split(a,b,z).size()); assertEquals(5400,new PeriodSplitter().split(a,b,z).stream().mapToLong(s->s.durationSeconds).sum());
 }
 @Test public void goalsHonorMinimumAndMaximum() {
  GoalCalculator c=new GoalCalculator(); assertTrue(c.calculate(400,1000,3000,CategoryEntity.GOAL_TYPE_MINIMUM).achieved); assertTrue(c.calculate(200,1000,3000,CategoryEntity.GOAL_TYPE_MAXIMUM).achieved); assertFalse(c.calculate(400,1000,3000,CategoryEntity.GOAL_TYPE_MAXIMUM).achieved);
 }
 @Test public void weekIdMatchesAdrEpoch() { assertEquals(253,new WeekCalendar().sequentialWeek(LocalDate.of(2026,9,6))); }
 @Test public void formatsAreStable() { DurationFormatter f=new DurationFormatter(); assertEquals("01:02",f.hhMm(3750)); assertEquals("01:02:30",f.csv(3750)); assertEquals("1.04",f.decimalHours(3750)); }
}
