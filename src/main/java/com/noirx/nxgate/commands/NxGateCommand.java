package com.noirx.nxgate.commands;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.commands.subcommands.*;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
public class NxGateCommand implements CommandExecutor, TabCompleter {
    private final NxGate plugin;
    private final HelpCommand     helpCmd;
    private final ReloadCommand   reloadCmd;
    private final OpenCommand     openCmd;
    private final CloseCommand    closeCmd;
    private final StatusCommand   statusCmd;
    private final NextCommand     nextCmd;
    private final ScheduleCommand scheduleCmd;
    private final TimerCommand    timerCmd;
    public NxGateCommand(@NotNull NxGate plugin) {
        this.plugin      = plugin;
        this.helpCmd     = new HelpCommand(plugin);
        this.reloadCmd   = new ReloadCommand(plugin);
        this.openCmd     = new OpenCommand(plugin);
        this.closeCmd    = new CloseCommand(plugin);
        this.statusCmd   = new StatusCommand(plugin);
        this.nextCmd     = new NextCommand(plugin);
        this.scheduleCmd = new ScheduleCommand(plugin);
        this.timerCmd    = new TimerCommand(plugin);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) { helpCmd.execute(sender); return true; }
        String sub       = args[0].toLowerCase();
        String[] remaining = Arrays.copyOfRange(args, 1, args.length);
        switch (sub) {
            case "help"     -> helpCmd.execute(sender);
            case "reload"   -> reloadCmd.execute(sender);
            case "open"     -> openCmd.execute(sender, remaining);
            case "close"    -> closeCmd.execute(sender, remaining);
            case "status"   -> statusCmd.execute(sender, remaining);
            case "next"     -> nextCmd.execute(sender, remaining);
            case "schedule" -> scheduleCmd.execute(sender, remaining);
            case "timer"    -> timerCmd.execute(sender, remaining);
            default         -> sender.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("unknown-command")));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(args[0], "help", "reload", "open", "close", "status", "next", "schedule", "timer");
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (List.of("open", "close", "status", "next").contains(sub)) return filterPortals(args[1]);
            if (sub.equals("schedule")) return filter(args[1], "list", "set", "remove");
            if (sub.equals("timer")) return filter(args[1], "extend");
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("schedule") && List.of("set", "remove").contains(args[1].toLowerCase()))
                return filterPortals(args[2]);
            if (sub.equals("timer") && args[1].equalsIgnoreCase("extend"))
                return filterPortals(args[2]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("schedule") && args[1].equalsIgnoreCase("set")) {
            return filter(args[3], "monday", "tuesday", "wednesday", "thursday",
                    "friday", "saturday", "sunday", "friday,saturday,sunday");
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("timer") && args[1].equalsIgnoreCase("extend")) {
            return filter(args[3], "30s", "1m", "5m", "10m", "30m", "1h", "2h");
        }
        return List.of();
    }
    private List<String> filterPortals(String input) {
        return Arrays.stream(PortalType.values())
                .map(PortalType::getId)
                .filter(id -> id.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
    private List<String> filter(String input, String... options) {
        return Arrays.stream(options)
                .filter(o -> o.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}