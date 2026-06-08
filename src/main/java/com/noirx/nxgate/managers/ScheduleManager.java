package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.models.ScheduleEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
public class ScheduleManager {
    private final NxGate plugin;
    private final List<ScheduleEntry> entries = new CopyOnWriteArrayList<>();
    private ZoneId zone;
    public ScheduleManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
        reload();
    }
    public void reload() {
        entries.clear();
        String tz = plugin.getConfigManager().getTimezone();
        try {
            zone = ZoneId.of(tz);
        } catch (DateTimeException e) {
            plugin.getLogger().warning("Invalid timezone '" + tz + "', defaulting to UTC.");
            zone = ZoneId.of("UTC");
        }
        entries.addAll(plugin.getConfigManager().loadSchedules());
        plugin.getLogger().info("Loaded " + entries.size() + " schedule entr" + (entries.size() == 1 ? "y" : "ies") + ".");
    }
    public void addSchedule(@NotNull ScheduleEntry entry) {
        entries.removeIf(e -> e.getPortal() == entry.getPortal()
                && e.getRepeatMode() == ScheduleEntry.RepeatMode.WEEKLY);
        entries.add(entry);
    }
    public boolean removeSchedules(@NotNull PortalType portal) {
        return entries.removeIf(e -> e.getPortal() == portal);
    }
    @NotNull
    public List<ScheduleEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    @NotNull
    public List<ScheduleEntry> getEntries(@NotNull PortalType portal) {
        List<ScheduleEntry> result = new ArrayList<>();
        for (ScheduleEntry e : entries) {
            if (e.getPortal() == portal) result.add(e);
        }
        return result;
    }
    public void tick() {
        if (!plugin.getConfigManager().isSchedulesEnabled()) return;
        ZonedDateTime now      = ZonedDateTime.now(zone);
        LocalDateTime localNow = now.toLocalDateTime();
        LocalTime localTime    = localNow.toLocalTime().withSecond(0).withNano(0);
        DayOfWeek today        = localNow.getDayOfWeek();
        for (ScheduleEntry entry : entries) {
            switch (entry.getRepeatMode()) {
                case WEEKLY, DAILY -> {
                    if (!entry.getDays().contains(today)) break;
                    if (entry.getOpenTime() != null && localTime.equals(entry.getOpenTime())) {
                        triggerOpen(entry.getPortal());
                    }
                    if (entry.getCloseTime() != null && localTime.equals(entry.getCloseTime())) {
                        triggerClose(entry.getPortal());
                    }
                }
                case ONE_TIME -> {
                    if (entry.getOpenDateTime() != null) {
                        LocalDateTime openAt = entry.getOpenDateTime().withSecond(0).withNano(0);
                        if (localNow.withSecond(0).withNano(0).equals(openAt)) triggerOpen(entry.getPortal());
                    }
                    if (entry.getCloseDateTime() != null) {
                        LocalDateTime closeAt = entry.getCloseDateTime().withSecond(0).withNano(0);
                        if (localNow.withSecond(0).withNano(0).equals(closeAt)) triggerClose(entry.getPortal());
                    }
                }
                default -> {}
            }
        }
    }
    @Nullable
    public String getNextOpeningInfo(@NotNull PortalType portal) {
        ZonedDateTime now      = ZonedDateTime.now(zone);
        ZonedDateTime earliest = null;
        for (ScheduleEntry entry : entries) {
            if (entry.getPortal() != portal) continue;
            if (entry.getRepeatMode() == ScheduleEntry.RepeatMode.ONE_TIME
                    && entry.getOpenDateTime() != null) {
                ZonedDateTime openDT = entry.getOpenDateTime().atZone(zone);
                if (openDT.isAfter(now) && (earliest == null || openDT.isBefore(earliest))) {
                    earliest = openDT;
                }
            } else if (entry.getOpenTime() != null) {
                for (int offset = 0; offset < 8; offset++) {
                    ZonedDateTime candidate = now.plusDays(offset)
                            .withHour(entry.getOpenTime().getHour())
                            .withMinute(entry.getOpenTime().getMinute())
                            .withSecond(0).withNano(0);
                    if (entry.getRepeatMode() == ScheduleEntry.RepeatMode.DAILY
                            || entry.getDays().contains(candidate.getDayOfWeek())) {
                        if (candidate.isAfter(now)) {
                            if (earliest == null || candidate.isBefore(earliest)) earliest = candidate;
                            break;
                        }
                    }
                }
            }
        }
        if (earliest == null) return null;
        String day  = earliest.getDayOfWeek().name().charAt(0)
                + earliest.getDayOfWeek().name().substring(1).toLowerCase();
        String time = earliest.format(DateTimeFormatter.ofPattern("HH:mm"));
        long remaining = Duration.between(now, earliest).toMillis();
        return day + " " + time + "|" + formatRemaining(remaining);
    }
    private void triggerOpen(@NotNull PortalType portal) {
        plugin.getPortalManager().openPortal(portal, null, null);
    }
    private void triggerClose(@NotNull PortalType portal) {
        plugin.getPortalManager().closePortal(portal, null, "Scheduled close");
    }
    @NotNull
    private String formatRemaining(long millis) {
        long days    = millis / 86400000L;
        long hours   = (millis % 86400000L) / 3600000L;
        long minutes = (millis % 3600000L)  / 60000L;
        StringBuilder sb = new StringBuilder();
        if (days > 0)    sb.append(days).append(days    == 1 ? " day "    : " days ");
        if (hours > 0)   sb.append(hours).append(hours   == 1 ? " hour "   : " hours ");
        if (minutes > 0) sb.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        return sb.toString().trim();
    }
}