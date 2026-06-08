package com.noirx.nxgate.config;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.models.ScheduleEntry;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
public class ConfigManager {
    private final NxGate plugin;
    private FileConfiguration cfg;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
    public ConfigManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.cfg = plugin.getConfig();
    }
    public void reload() {
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
    }
    public @NotNull String getLanguage()  { return cfg.getString("language", "en_US"); }
    public @NotNull String getTimezone()  { return cfg.getString("timezone", "UTC"); }
    public boolean isBroadcastEnabled()   { return cfg.getBoolean("broadcast.enabled", true); }
    public boolean isBroadcastChat()      { return cfg.getBoolean("broadcast.chat", true); }
    public boolean isBroadcastActionBar() { return cfg.getBoolean("broadcast.actionbar", true); }
    public boolean isBroadcastTitle()     { return cfg.getBoolean("broadcast.title", true); }
    public boolean isBroadcastSubtitle()  { return cfg.getBoolean("broadcast.subtitle", true); }
    public boolean isBossBarEnabled()     { return cfg.getBoolean("bossbar.enabled", true); }
    public boolean isBossBarShowOnJoin()  { return cfg.getBoolean("bossbar.show-on-join", true); }
    public @NotNull BarColor getBossBarColor() {
        return parseBarColor(cfg.getString("bossbar.color", "PURPLE"));
    }
    public @NotNull BarColor getBossBarColor(@NotNull PortalType type) {
        String key = "bossbar.portal-colors." + type.getId();
        if (cfg.contains(key)) return parseBarColor(cfg.getString(key, "PURPLE"));
        return getBossBarColor();
    }
    public @NotNull BarStyle getBossBarStyle() {
        try { return BarStyle.valueOf(cfg.getString("bossbar.style", "SEGMENTED_10")); }
        catch (IllegalArgumentException e) { return BarStyle.SEGMENTED_10; }
    }
    public boolean isSoundsEnabled()    { return cfg.getBoolean("sounds.enabled", true); }
    public @NotNull Sound getOpenSound()  { return parseSound(cfg.getString("sounds.open.sound",  "BLOCK_NOTE_BLOCK_PLING")); }
    public float getOpenVolume()          { return (float) cfg.getDouble("sounds.open.volume",  1.0); }
    public float getOpenPitch()           { return (float) cfg.getDouble("sounds.open.pitch",   1.4); }
    public @NotNull Sound getCloseSound() { return parseSound(cfg.getString("sounds.close.sound", "ENTITY_WITHER_SPAWN")); }
    public float getCloseVolume()         { return (float) cfg.getDouble("sounds.close.volume", 0.5); }
    public float getClosePitch()          { return (float) cfg.getDouble("sounds.close.pitch",  1.2); }
    public boolean isKickPlayersEnabled() { return cfg.getBoolean("kick_players.enabled", true); }
    public int getTeleportDelay()         { return cfg.getInt("kick_players.teleport_delay", 0); }
    public enum KickMode { KICK, TELEPORT }
    public @NotNull KickMode getKickMode() {
        try { return KickMode.valueOf(cfg.getString("kick_players.mode", "TELEPORT")); }
        catch (IllegalArgumentException e) { return KickMode.TELEPORT; }
    }
    public enum TeleportDestination { SPAWN, WORLD_SPAWN, LOCATION, ENTRY, HOME }
    public @NotNull TeleportDestination getTeleportDestination() {
        try { return TeleportDestination.valueOf(cfg.getString("kick_players.teleport_to", "ENTRY")); }
        catch (IllegalArgumentException e) { return TeleportDestination.ENTRY; }
    }
    public @NotNull TeleportDestination getHomeFallback() {
        try { return TeleportDestination.valueOf(cfg.getString("kick_players.home_fallback", "WORLD_SPAWN")); }
        catch (IllegalArgumentException e) { return TeleportDestination.WORLD_SPAWN; }
    }
    public @Nullable Location getTeleportLocation() {
        ConfigurationSection sec = cfg.getConfigurationSection("kick_players.teleport_location");
        if (sec == null) return null;
        String worldName = sec.getString("world", "world");
        var world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;
        double x = sec.getDouble("x", 0);
        double y = sec.getDouble("y", 64);
        double z = sec.getDouble("z", 0);
        return new Location(world, x, y, z);
    }
    public boolean isReasonsEnabled()     { return cfg.getBoolean("reasons.enabled", true); }
    public boolean isSchedulesEnabled()   { return cfg.getBoolean("schedules.enabled", true); }
    public boolean isUpdateCheckerEnabled(){ return cfg.getBoolean("update-checker", true); }
    public boolean isBStatsEnabled()      { return cfg.getBoolean("bstats", true); }
    public boolean isPortalDefaultOpen(@NotNull PortalType type) {
        return cfg.getBoolean("portals." + type.getId() + ".open", true);
    }
    public @NotNull List<ScheduleEntry> loadSchedules() {
        List<ScheduleEntry> entries = new ArrayList<>();
        if (!isSchedulesEnabled()) return entries;
        ConfigurationSection root = cfg.getConfigurationSection("schedules");
        if (root == null) return entries;
        for (PortalType portal : PortalType.values()) {
            ConfigurationSection sec = root.getConfigurationSection(portal.getId());
            if (sec == null) continue;
            List<String> dayNames = sec.getStringList("days");
            String openStr  = sec.getString("open");
            String closeStr = sec.getString("close");
            if (dayNames.isEmpty() || openStr == null || closeStr == null) continue;
            Set<DayOfWeek> days = new LinkedHashSet<>();
            for (String dayName : dayNames) {
                try { days.add(DayOfWeek.valueOf(dayName.toUpperCase())); }
                catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Unknown day '" + dayName + "' in schedule for " + portal.getId());
                }
            }
            LocalTime open  = parseTime(openStr);
            LocalTime close = parseTime(closeStr);
            if (open == null || close == null || days.isEmpty()) continue;
            entries.add(new ScheduleEntry(portal, days, open, close));
        }
        ConfigurationSection specialSection = root.getConfigurationSection("special_dates");
        if (specialSection != null) {
            for (String portalId : specialSection.getKeys(false)) {
                PortalType portal = PortalType.fromId(portalId);
                if (portal == null) continue;
                List<Map<?, ?>> list = specialSection.getMapList(portalId);
                for (Map<?, ?> map : list) {
                    String date  = String.valueOf(map.get("date"));
                    String open  = String.valueOf(map.get("open"));
                    String close = String.valueOf(map.get("close"));
                    LocalDateTime openDT  = parseDateTime(date + " " + open);
                    LocalDateTime closeDT = parseDateTime(date + " " + close);
                    if (openDT != null && closeDT != null) {
                        entries.add(new ScheduleEntry(portal, openDT, closeDT));
                    }
                }
            }
        }
        ConfigurationSection eventsSection = root.getConfigurationSection("events");
        if (eventsSection != null) {
            for (String eventName : eventsSection.getKeys(false)) {
                ConfigurationSection ev = eventsSection.getConfigurationSection(eventName);
                if (ev == null) continue;
                PortalType portal = PortalType.fromId(ev.getString("portal", ""));
                if (portal == null) continue;
                LocalDateTime openDT  = parseDateTime(ev.getString("open",  ""));
                LocalDateTime closeDT = parseDateTime(ev.getString("close", ""));
                if (openDT != null && closeDT != null) {
                    entries.add(new ScheduleEntry(portal, openDT, closeDT));
                }
            }
        }
        return entries;
    }
    private @Nullable LocalTime parseTime(String s) {
        try { return LocalTime.parse(s, TIME_FMT); }
        catch (DateTimeParseException e) {
            plugin.getLogger().log(Level.WARNING, "Cannot parse time: " + s);
            return null;
        }
    }
    private @Nullable LocalDateTime parseDateTime(String s) {
        try { return LocalDateTime.parse(s, DATE_TIME_FMT); }
        catch (DateTimeParseException e) {
            plugin.getLogger().log(Level.WARNING, "Cannot parse datetime: " + s);
            return null;
        }
    }
    private @NotNull Sound parseSound(String name) {
        try { return Sound.valueOf(name); }
        catch (IllegalArgumentException e) { return Sound.BLOCK_NOTE_BLOCK_PLING; }
    }
    private @NotNull BarColor parseBarColor(String name) {
        try { return BarColor.valueOf(name); }
        catch (IllegalArgumentException e) { return BarColor.PURPLE; }
    }
}