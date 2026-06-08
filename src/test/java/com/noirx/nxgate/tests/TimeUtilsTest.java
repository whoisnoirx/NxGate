package com.noirx.nxgate.tests;
import com.noirx.nxgate.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
class TimeUtilsTest {
    private static final ZoneId UTC = ZoneId.of("UTC");
    @Test void hours()    { assertEquals(TimeUnit.HOURS.toMillis(2),   TimeUtils.parseDuration("2h", UTC)); }
    @Test void minutes()  { assertEquals(TimeUnit.MINUTES.toMillis(30),TimeUtils.parseDuration("30m", UTC)); }
    @Test void seconds()  { assertEquals(TimeUnit.SECONDS.toMillis(45),TimeUtils.parseDuration("45s", UTC)); }
    @Test void days()     { assertEquals(TimeUnit.DAYS.toMillis(1),    TimeUtils.parseDuration("1d", UTC)); }
    @Test
    void daysAndHours() {
        long expected = TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(12);
        assertEquals(expected, TimeUtils.parseDuration("1d12h", UTC));
    }
    @Test
    void complex() {
        long expected = TimeUnit.DAYS.toMillis(1)
                + TimeUnit.HOURS.toMillis(2)
                + TimeUnit.MINUTES.toMillis(30)
                + TimeUnit.SECONDS.toMillis(15);
        assertEquals(expected, TimeUtils.parseDuration("1d2h30m15s", UTC));
    }
    @Test void nullInput()    { assertEquals(-1L, TimeUtils.parseDuration(null, UTC)); }
    @Test void emptyInput()   { assertEquals(-1L, TimeUtils.parseDuration("", UTC)); }
    @Test void zeroInput()    { assertEquals(-1L, TimeUtils.parseDuration("0h", UTC)); }
    @Test void invalidInput() { assertEquals(-1L, TimeUtils.parseDuration("xyz", UTC)); }
    @Test
    void caseInsensitive() {
        assertEquals(TimeUtils.parseDuration("2h", UTC), TimeUtils.parseDuration("2H", UTC));
    }
    @Test
    void time12hSimpleAM() {
        long millis = TimeUtils.parseDuration("5AM", UTC);
        assertTrue(millis > 0, "5AM should return positive millis");
        assertTrue(millis <= TimeUnit.DAYS.toMillis(1) + 1000,
                "5AM should be within 24 hours");
    }
    @Test
    void time12hSimplePM() {
        long millis = TimeUtils.parseDuration("5PM", UTC);
        assertTrue(millis > 0, "5PM should return positive millis");
    }
    @Test
    void time12hWithMinutes() {
        long millis = TimeUtils.parseDuration("5:30PM", UTC);
        assertTrue(millis > 0, "5:30PM should return positive millis");
    }
    @Test
    void time12hMidnight() {
        long millis = TimeUtils.parseDuration("12AM", UTC);
        assertTrue(millis > 0, "12AM should return positive millis");
    }
    @Test
    void time12hNoon() {
        long millis = TimeUtils.parseDuration("12PM", UTC);
        assertTrue(millis > 0, "12PM should return positive millis");
    }
    @Test
    void time12hCaseInsensitive() {
        long lower = TimeUtils.parseDuration("5am", UTC);
        long upper = TimeUtils.parseDuration("5AM", UTC);
        assertTrue(Math.abs(lower - upper) < 1000);
    }
    @Test
    void time24h() {
        long millis = TimeUtils.parseDuration("17:30", UTC);
        assertTrue(millis > 0, "17:30 should return positive millis");
        assertTrue(millis <= TimeUnit.DAYS.toMillis(1) + 1000);
    }
    @Test
    void time24hMidnight() {
        long millis = TimeUtils.parseDuration("00:00", UTC);
        assertTrue(millis > 0);
    }
    @Test
    void time24hInvalid() {
        assertEquals(-1L, TimeUtils.parseDuration("25:00", UTC));
    }
    @Test
    void dateDotFormatFuture() {
        long millis = TimeUtils.parseDuration("1.1.50", UTC); 
        assertTrue(millis > 0, "Future date should give positive millis");
        assertTrue(millis > TimeUnit.DAYS.toMillis(365L * 10));
    }
    @Test
    void dateDotFormatPast() {
        long millis = TimeUtils.parseDuration("1.1.20", UTC); 
        assertEquals(-1L, millis, "Past date should return -1");
    }
    @Test
    void dateSlashFormat() {
        long millis = TimeUtils.parseDuration("1/1/50", UTC);
        assertTrue(millis > 0, "Future slash-date should give positive millis");
    }
    @Test
    void dateIsoFormat() {
        long millis = TimeUtils.parseDuration("2050-01-01", UTC);
        assertTrue(millis > 0, "ISO future date should give positive millis");
    }
    @Test
    void dateIsoFormatPast() {
        long millis = TimeUtils.parseDuration("2020-01-01", UTC);
        assertEquals(-1L, millis, "ISO past date should return -1");
    }
    @Test
    void dateTwoDigitYearExpansion() {
        long millis = TimeUtils.parseDuration("1.1.50", UTC);
        assertTrue(millis > TimeUnit.DAYS.toMillis(365L * 10),
                "Year 50 should expand to 2050");
    }
    @Test
    void formatCompact() {
        long millis = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(59) + TimeUnit.SECONDS.toMillis(59);
        assertEquals("01:59:59", TimeUtils.formatDuration(millis, true));
    }
    @Test
    void formatCompactWithDays() {
        long millis = TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(30);
        assertEquals("1d 02:30:00", TimeUtils.formatDuration(millis, true));
    }
    @Test
    void formatVerboseSingular() {
        long millis = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(1);
        String result = TimeUtils.formatDuration(millis, false);
        assertTrue(result.contains("1 hour"));
        assertTrue(result.contains("1 minute"));
        assertTrue(result.contains("1 second"));
    }
    @Test
    void formatVerbosePlural() {
        long millis = TimeUnit.HOURS.toMillis(2) + TimeUnit.MINUTES.toMillis(5);
        String result = TimeUtils.formatDuration(millis, false);
        assertTrue(result.contains("2 hours"));
        assertTrue(result.contains("5 minutes"));
    }
    @Test void formatZeroCompact()  { assertEquals("00:00:00", TimeUtils.formatDuration(0, true)); }
    @Test void formatZeroVerbose()  { assertEquals("0 seconds", TimeUtils.formatDuration(0, false)); }
    @Test void formatNegative()     { assertEquals("00:00:00", TimeUtils.formatDuration(-1000, true)); }
    @Test void dayMonday() { assertEquals("Monday", TimeUtils.formatDay(DayOfWeek.MONDAY)); }
    @Test void daySunday() { assertEquals("Sunday", TimeUtils.formatDay(DayOfWeek.SUNDAY)); }
    @Test void dayFriday() { assertEquals("Friday", TimeUtils.formatDay(DayOfWeek.FRIDAY)); }
}