package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.Duration;
public class BroadcastManager {
    private final NxGate plugin;
    public BroadcastManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    public void broadcastOpen(@NotNull PortalType type, @Nullable CommandSender opener) {
        if (!plugin.getConfigManager().isBroadcastEnabled()) return;
        String portal = type.getDisplayName();
        String rawChat   = plugin.getLangManager().getRaw("broadcast-open");
        String rawNoPfx  = plugin.getLangManager().getRawNoPfx("broadcast-open");
        String rawSub    = plugin.getLangManager().getRawNoPfx("broadcast-open-subtitle");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.getConfigManager().isBroadcastChat()) {
                p.sendMessage(MessageUtils.parse(rawChat, "portal", portal));
            }
            if (plugin.getConfigManager().isBroadcastActionBar()) {
                p.sendActionBar(MessageUtils.parse(rawNoPfx, "portal", portal));
            }
            if (plugin.getConfigManager().isBroadcastTitle()) {
                p.showTitle(Title.title(
                        MessageUtils.parse(rawNoPfx, "portal", portal),
                        MessageUtils.parse(rawSub, "portal", portal),
                        Title.Times.times(Duration.ofMillis(400), Duration.ofSeconds(3), Duration.ofMillis(600))
                ));
            }
        }
    }
    public void broadcastClose(@NotNull PortalType type, @Nullable CommandSender closer,
                                @Nullable String reason) {
        if (!plugin.getConfigManager().isBroadcastEnabled()) return;
        String portal = type.getDisplayName();
        boolean hasReason = plugin.getConfigManager().isReasonsEnabled()
                && reason != null && !reason.isBlank();
        String chatKey   = hasReason ? "broadcast-close-reason" : "broadcast-close";
        String titleKey  = hasReason ? "broadcast-close-reason" : "broadcast-close";
        String subKey    = "broadcast-close-subtitle";
        String rawChat  = plugin.getLangManager().getRaw(chatKey);
        String rawNoPfx = plugin.getLangManager().getRawNoPfx(titleKey);
        String rawSub   = plugin.getLangManager().getRawNoPfx(subKey);
        String[] kv = hasReason
                ? new String[]{"portal", portal, "reason", reason}
                : new String[]{"portal", portal};
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.getConfigManager().isBroadcastChat()) {
                p.sendMessage(MessageUtils.parse(rawChat, kv));
            }
            if (plugin.getConfigManager().isBroadcastActionBar()) {
                p.sendActionBar(MessageUtils.parse(rawNoPfx, kv));
            }
            if (plugin.getConfigManager().isBroadcastTitle()) {
                p.showTitle(Title.title(
                        MessageUtils.parse(rawNoPfx, kv),
                        MessageUtils.parse(rawSub, kv),
                        Title.Times.times(Duration.ofMillis(400), Duration.ofSeconds(4), Duration.ofMillis(600))
                ));
            }
        }
    }
}