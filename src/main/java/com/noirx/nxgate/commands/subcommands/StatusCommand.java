package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.lang.LanguageManager;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import com.noirx.nxgate.utils.TimeUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
public class StatusCommand {
    private final NxGate plugin;
    public StatusCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        LanguageManager lang = plugin.getLangManager();
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("status-header")));
        PortalType[] targets = args.length > 0 && PortalType.fromId(args[0]) != null
                ? new PortalType[]{ PortalType.fromId(args[0]) }
                : PortalType.values();
        for (PortalType type : targets) {
            if (type == null) continue;
            PortalState state = plugin.getPortalManager().getState(type);
            String statusText = buildStatusText(state, lang);
            String line = lang.getRawNoPfx("status-portal")
                    .replace("%portal%", type.getIconName())
                    .replace("%status%", statusText);
            sender.sendMessage(MessageUtils.parse(line));
        }
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("status-footer")));
    }
    private String buildStatusText(@NotNull PortalState state, @NotNull LanguageManager lang) {
        if (!state.isOpen()) return lang.getRawNoPfx("status-closed");
        if (state.hasTimer()) {
            String timeStr = TimeUtils.formatDuration(state.getRemainingMillis(), true);
            return lang.getRawNoPfx("status-timed").replace("%time%", timeStr);
        }
        return lang.getRawNoPfx("status-open");
    }
}