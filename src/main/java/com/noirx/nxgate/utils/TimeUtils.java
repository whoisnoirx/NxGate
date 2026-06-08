package com.noirx.nxgate.utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public final class TimeUtils {
    private TimeUtils() {}
    private static final Pattern DURATION_PATTERN =
            Pattern.compile("(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_12H_PATTERN =
            Pattern.compile("^(\\d{1,2})(?::(\\d{2}))?\\s*(AM|PM)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_24H_PATTERN =
            Pattern.compile("^(\\d{1,2}):(\\d{2})$");
    private static final Pattern DATE_PATTERN =
            Pattern.compile("^(\\d{1,2})[./](\\d{1,2})[./](\\d{2,4})$");
    private static final Pattern ISO_DATE_PATTERN =
            Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})$");
    public static long parseDuration(@Nullable String input) {
        return parseDuration(input, ZoneId.systemDefault());
    }
    public static long parseDuration(@Nullable String input, @Nullable ZoneId zoneId) {
        if (input == null || input.isBlank()) return -1L;
        String trimmed = input.trim();
        ZoneId zone = (zoneId != null) ? zoneId : ZoneId.systemDefault();
        Matcher m12 = TIME_12H_PATTERN.matcher(trimmed);
        if (m12.matches()) return parseAbsoluteTime(toLocalTime12(m12), zone);
        Matcher m24 = TIME_24H_PATTERN.matcher(trimmed);
        if (m24.matches()) {
            int hour = Integer.parseInt(m24.group(1));
            int min  = Integer.parseInt(m24.group(2));
            if (hour >= 0 && hour <= 23 && min >= 0 && min <= 59) {
                return parseAbsoluteTime(LocalTime.of(hour, min), zone);
            }
        }
        Matcher mDate = DATE_PATTERN.matcher(trimmed);
        if (mDate.matches()) {
            int day  = Integer.parseInt(mDate.group(1));
            int mon  = Integer.parseInt(mDate.group(2));
            int year = parseYear(mDate.group(3));
            return parseAbsoluteDate(day, mon, year, zone);
        }
        Matcher mIso = ISO_DATE_PATTERN.matcher(trimmed);
        if (mIso.matches()) {
            int year = Integer.parseInt(mIso.group(1));
            int mon  = Integer.parseInt(mIso.group(2));
            int day  = Integer.parseInt(mIso.group(3));
            return parseAbsoluteDate(day, mon, year, zone);
        }
        return parseDurationString(trimmed);
    }
    @NotNull
    public static String formatDuration(long millis, boolean compact) {
        if (millis <= 0) return compact ? "00:00:00" : "0 seconds";
        Duration d = Duration.ofMillis(millis);
        long days    = d.toDays();
        long hours   = d.toHoursPart();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();
        if (compact) {
            if (days > 0) return String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        StringBuilder sb = new StringBuilder();
        if (days > 0)    appendUnit(sb, days,    "day");
        if (hours > 0)   appendUnit(sb, hours,   "hour");
        if (minutes > 0) appendUnit(sb, minutes, "minute");
        if (seconds > 0) appendUnit(sb, seconds, "second");
        return sb.length() > 0 ? sb.toString().trim() : "0 seconds";
    }
    private static void appendUnit(StringBuilder sb, long value, String unit) {
        sb.append(value).append(' ').append(unit).append(value == 1 ? "" : "s").append(' ');
    }
    @NotNull
    public static String formatDay(@NotNull DayOfWeek day) {
        String name = day.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
    private static long parseDurationString(@NotNull String input) {
        Matcher m = DURATION_PATTERN.matcher(input);
        if (!m.matches()) return -1L;
        long days    = parseLong(m.group(1));
        long hours   = parseLong(m.group(2));
        long minutes = parseLong(m.group(3));
        long seconds = parseLong(m.group(4));
        long millis = TimeUnit.DAYS.toMillis(days)
                + TimeUnit.HOURS.toMillis(hours)
                + TimeUnit.MINUTES.toMillis(minutes)
                + TimeUnit.SECONDS.toMillis(seconds);
        return millis > 0 ? millis : -1L;
    }
    @Nullable
    private static LocalTime toLocalTime12(@NotNull Matcher m) {
        try {
            int hour = Integer.parseInt(m.group(1));
            int min  = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
            String period = m.group(3).toUpperCase();
            if (hour < 1 || hour > 12 || min < 0 || min > 59) return null;
            if ("PM".equals(period) && hour != 12) hour += 12;
            if ("AM".equals(period) && hour == 12) hour = 0;
            return LocalTime.of(hour, min);
        } catch (DateTimeException | NumberFormatException e) {
            return null;
        }
    }
    private static long parseAbsoluteTime(@Nullable LocalTime target, @NotNull ZoneId zone) {
        if (target == null) return -1L;
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime todayTarget = now.toLocalDate().atTime(target).atZone(zone);
        if (!todayTarget.isAfter(now)) todayTarget = todayTarget.plusDays(1);
        long millis = java.time.Duration.between(now, todayTarget).toMillis();
        return millis > 0 ? millis : -1L;
    }
    private static long parseAbsoluteDate(int day, int month, int year, @NotNull ZoneId zone) {
        try {
            LocalDate date = LocalDate.of(year, month, day);
            ZonedDateTime target = date.atStartOfDay(zone).plusDays(1);
            ZonedDateTime now    = ZonedDateTime.now(zone);
            long millis = java.time.Duration.between(now, target).toMillis();
            return millis > 0 ? millis : -1L;
        } catch (DateTimeException e) {
            return -1L;
        }
    }
    private static int parseYear(String s) {
        int y = Integer.parseInt(s);
        if (s.length() <= 2) y = 2000 + y;
        return y;
    }
    private static long parseLong(@Nullable String s) {
        if (s == null) return 0L;
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return 0L; }
    }
}