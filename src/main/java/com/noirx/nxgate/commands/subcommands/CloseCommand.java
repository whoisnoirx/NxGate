package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
public class CloseCommand {
    private final NxGate plugin;
    public CloseCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("nxgate.close")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("no-permission")));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRawNoPfx("help-close")));
            return;
        }
        PortalType type = PortalType.fromId(args[0]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-portal"), "portal", args[0]));
            return;
        }
        String reason = null;
        if (plugin.getConfigManager().isReasonsEnabled() && args.length >= 2) {
            String[] parts = new String[args.length - 1];
            System.arraycopy(args, 1, parts, 0, parts.length);
            reason = String.join(" ", parts);
        }
        boolean closed = plugin.getPortalManager().closePortal(type, sender, reason);
        String portal  = type.getDisplayName();
        if (!closed) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-already-closed"), "portal", portal));
            return;
        }
        boolean hasReason = reason != null && !reason.isBlank();
        if (hasReason) {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-closed-reason"),
                    "portal", portal, "reason", reason));
        } else {
            sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-closed"), "portal", portal));
        }
    }
}