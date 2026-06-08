package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.lang.LanguageManager;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;
public class HelpCommand {
    private final NxGate plugin;
    public HelpCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender) {
        LanguageManager lang = plugin.getLangManager();
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("help-header")));
        for (String key : List.of(
                "help-open", "help-close", "help-status", "help-next",
                "help-schedule-list", "help-schedule-set", "help-schedule-remove",
                "help-timer-extend", "help-reload")) {
            sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx(key)));
        }
        sender.sendMessage(MessageUtils.parse(lang.getRawNoPfx("help-footer")));
    }
}