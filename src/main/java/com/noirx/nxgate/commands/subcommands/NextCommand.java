package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.lang.LanguageManager;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
public class NextCommand {
    private final NxGate plugin;
    public NextCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        LanguageManager lang = plugin.getLangManager();
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("help-next")));
            return;
        }
        PortalType type = PortalType.fromId(args[0]);
        if (type == null) {
            sender.sendMessage(MessageUtils.parse(
                    lang.getRaw("unknown-portal"), "portal", args[0]));
            return;
        }
        String portal = type.getDisplayName();
        if (plugin.getPortalManager().isOpen(type)) {
            sender.sendMessage(MessageUtils.parse(lang.getRaw("next-currently-open"), "portal", portal));
            return;
        }
        String info = plugin.getScheduleManager().getNextOpeningInfo(type);
        if (info == null) {
            sender.sendMessage(MessageUtils.parse(lang.getRaw("next-no-schedule"), "portal", portal));
            return;
        }
        String[] parts   = info.split("\\|", 2);
        String dayTime   = parts[0].trim();
        String remaining = parts.length > 1 ? parts[1].trim() : "";
        String day       = dayTime.contains(" ") ? dayTime.split(" ")[0] : dayTime;
        String openTime  = dayTime.contains(" ") ? dayTime.split(" ")[1] : "";
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("next-header")));
        sender.sendMessage(MessageUtils.parse(
                lang.getRawNoPfx("next-schedule"),
                "portal", portal, "day", day, "time", openTime));
        if (!remaining.isBlank()) {
            sender.sendMessage(MessageUtils.parse(
                    lang.getRawNoPfx("next-remaining"), "remaining", remaining));
        }
    }
}