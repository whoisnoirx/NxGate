package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.lang.LanguageManager;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.models.ScheduleEntry;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
public class ScheduleCommand {
    private final NxGate plugin;
    public ScheduleCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("nxgate.schedule")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("no-permission")));
            return;
        }
        if (args.length == 0) { list(sender); return; }
        switch (args[0].toLowerCase()) {
            case "list"   -> list(sender);
            case "set"    -> set(sender, Arrays.copyOfRange(args, 1, args.length));
            case "remove" -> remove(sender, Arrays.copyOfRange(args, 1, args.length));
            default       -> list(sender);
        }
    }
    private void list(@NotNull CommandSender sender) {
        LanguageManager lang = plugin.getLangManager();
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("schedule-list-header")));
        List<ScheduleEntry> entries = plugin.getScheduleManager().getEntries();
        if (entries.isEmpty()) {
            sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("schedule-list-empty")));
        } else {
            for (ScheduleEntry e : entries) {
                String days = e.getDays().stream()
                        .map(d -> d.name().charAt(0) + d.name().substring(1).toLowerCase())
                        .collect(Collectors.joining(", "));
                String open  = e.getOpenTime() != null ? e.getOpenTime().toString()
                        : (e.getOpenDateTime() != null ? e.getOpenDateTime().toString() : "?");
                String close = e.getCloseTime() != null ? e.getCloseTime().toString()
                        : (e.getCloseDateTime() != null ? e.getCloseDateTime().toString() : "?");
                sender.sendMessage(MessageUtils.parse(
                        lang.getRawNoPfx("schedule-list-entry"),
                        "portal", e.getPortal().getDisplayName(),
                        "days", days, "open", open, "close", close));
            }
        }
    }
    private void set(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-schedule-set")));
            return;
        }
        PortalType type = PortalType.fromId(args[0]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-portal"), "portal", args[0]));
            return;
        }
        Set<DayOfWeek> days = new LinkedHashSet<>();
        for (String dayStr : args[1].split(",")) {
            try { days.add(DayOfWeek.valueOf(dayStr.trim().toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        if (days.isEmpty()) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-schedule-set")));
            return;
        }
        LocalTime openTime, closeTime;
        try {
            openTime  = LocalTime.parse(args[2]);
            closeTime = LocalTime.parse(args[3]);
        } catch (DateTimeParseException e) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-schedule-set")));
            return;
        }
        plugin.getScheduleManager().addSchedule(new ScheduleEntry(type, days, openTime, closeTime));
        sender.sendMessage(MessageUtils.parse(
                plugin.getLangManager().getRaw("schedule-set"), "portal", type.getDisplayName()));
    }
    private void remove(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-schedule-remove")));
            return;
        }
        PortalType type = PortalType.fromId(args[0]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-portal"), "portal", args[0]));
            return;
        }
        boolean removed = plugin.getScheduleManager().removeSchedules(type);
        String key = removed ? "schedule-removed" : "schedule-no-schedule";
        sender.sendMessage(MessageUtils.parse(
                plugin.getLangManager().getRaw(key), "portal", type.getDisplayName()));
    }
}