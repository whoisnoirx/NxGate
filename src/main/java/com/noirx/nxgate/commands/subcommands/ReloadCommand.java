package com.noirx.nxgate.commands.subcommands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
public class ReloadCommand {
    private final NxGate plugin;
    public ReloadCommand(@NotNull NxGate plugin) { this.plugin = plugin; }
    public void execute(@NotNull CommandSender sender) {
        if (!sender.hasPermission("nxgate.reload")) {
            sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("no-permission")));
            return;
        }
        plugin.getConfigManager().reload();
        plugin.getLangManager().reload();
        plugin.getScheduleManager().reload();
        sender.sendMessage(MessageUtils.parse(plugin.getLangManager().getRaw("plugin-reloaded")));
    }
}