package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import com.noirx.nxgate.utils.TimeUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
public class TimerCommand {
    private final NxGate plugin;
    public TimerCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("nxgate.timer")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("no-permission")));
            return;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("extend")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-timer-extend")));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-timer-extend")));
            return;
        }
        PortalType type = PortalType.fromId(args[1]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-portal"), "portal", args[1]));
            return;
        }
        if (!plugin.getPortalManager().isOpen(type)) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("timer-portal-closed"), "portal", type.getDisplayName()));
            return;
        }
        ZoneId zone;
        try { zone = ZoneId.of(plugin.getConfigManager().getTimezone()); }
        catch (Exception e) { zone = ZoneId.systemDefault(); }
        long extraMillis = TimeUtils.parseDuration(args[2], zone);
        if (extraMillis <= 0) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-timer-extend")));
            return;
        }
        boolean extended = plugin.getPortalManager().extendTimer(type, extraMillis);
        if (!extended) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("timer-not-running"), "portal", type.getDisplayName()));
            return;
        }
        String portal   = type.getDisplayName();
        String timeStr  = TimeUtils.formatDuration(extraMillis, false);
        long remaining  = plugin.getPortalManager().getState(type).getRemainingMillis();
        String totalStr = TimeUtils.formatDuration(remaining, true);
        sender.sendMessage(MessageUtils.parse(
                plugin.getLangManager().getRaw("timer-extended"),
                "portal", portal, "time", timeStr, "total", totalStr));
    }
}