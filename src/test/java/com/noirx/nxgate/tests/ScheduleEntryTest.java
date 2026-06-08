package com.noirx.nxgate.tests;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.models.ScheduleEntry;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
class ScheduleEntryTest {
    @Test
    void testWeeklySchedule() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        LocalTime open  = LocalTime.of(18, 0);
        LocalTime close = LocalTime.of(23, 0);
        ScheduleEntry entry = new ScheduleEntry(PortalType.END, days, open, close);
        assertEquals(PortalType.END, entry.getPortal());
        assertEquals(ScheduleEntry.RepeatMode.WEEKLY, entry.getRepeatMode());
        assertEquals(3, entry.getDays().size());
        assertTrue(entry.getDays().contains(DayOfWeek.FRIDAY));
        assertEquals(LocalTime.of(18, 0), entry.getOpenTime());
        assertEquals(LocalTime.of(23, 0), entry.getCloseTime());
        assertNull(entry.getOpenDateTime());
    }
    @Test
    void testDailySchedule() {
        LocalTime open  = LocalTime.of(8, 0);
        LocalTime close = LocalTime.of(20, 0);
        ScheduleEntry entry = new ScheduleEntry(PortalType.NETHER, open, close);
        assertEquals(ScheduleEntry.RepeatMode.DAILY, entry.getRepeatMode());
        assertEquals(7, entry.getDays().size()); 
    }
    @Test
    void testOneTimeSchedule() {
        LocalDateTime open  = LocalDateTime.of(2026, 8, 1, 18, 0);
        LocalDateTime close = LocalDateTime.of(2026, 8, 1, 23, 0);
        ScheduleEntry entry = new ScheduleEntry(PortalType.END, open, close);
        assertEquals(ScheduleEntry.RepeatMode.ONE_TIME, entry.getRepeatMode());
        assertEquals(open, entry.getOpenDateTime());
        assertEquals(close, entry.getCloseDateTime());
        assertNull(entry.getOpenTime());
        assertTrue(entry.getDays().isEmpty());
    }
    @Test
    void testDaysAreUnmodifiable() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);
        ScheduleEntry entry = new ScheduleEntry(PortalType.NETHER, days, LocalTime.NOON, LocalTime.MIDNIGHT);
        assertThrows(UnsupportedOperationException.class, () ->
                entry.getDays().add(DayOfWeek.TUESDAY));
    }
}