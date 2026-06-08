package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import com.noirx.nxgate.utils.TimeUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
public class OpenCommand {
    private final NxGate plugin;
    public OpenCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("nxgate.open")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("no-permission")));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-open")));
            return;
        }
        PortalType type = PortalType.fromId(args[0]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-portal"), "portal", args[0]));
            return;
        }
        Long durationMillis = null;
        if (args.length >= 2) {
            ZoneId zone;
            try { zone = ZoneId.of(plugin.getConfigManager().getTimezone()); }
            catch (Exception e) { zone = ZoneId.systemDefault(); }
            long parsed = TimeUtils.parseDuration(args[1], zone);
            if (parsed > 0) durationMillis = parsed;
        }
        boolean opened = plugin.getPortalManager().openPortal(type, sender, durationMillis);
        String portal  = type.getDisplayName();
        if (!opened) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-already-open"), "portal", portal));
            return;
        }
        if (durationMillis != null) {
            String timeStr = TimeUtils.formatDuration(durationMillis, false);
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-open-timed"),
                    "portal", portal, "time", timeStr));
        } else {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-opened"), "portal", portal));
        }
    }
}