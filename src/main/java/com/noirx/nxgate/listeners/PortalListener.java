package com.noirx.nxgate.listeners;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.api.events.PortalAccessDeniedEvent;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class PortalListener implements Listener {
    private final NxGate plugin;
    public PortalListener(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPortalUse(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        PortalType type = resolveType(event);
        if (type == null) return;
        if (plugin.getPortalManager().isOpen(type)) {
            plugin.getPlayerManager().saveEntryPoint(player.getUniqueId(), player.getLocation());
            return;
        }
        if (player.hasPermission("nxgate.bypass")) return;
        String reason = plugin.getPortalManager().getState(type).getCloseReason();
        PortalAccessDeniedEvent denied = new PortalAccessDeniedEvent(player, type, reason);
        plugin.getServer().getPluginManager().callEvent(denied);
        if (denied.isCancelled()) return;
        event.setCancelled(true);
        boolean hasReason = plugin.getConfigManager().isReasonsEnabled()
                && reason != null && !reason.isBlank();
        String portal = type.getDisplayName();
        if (hasReason) {
            player.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-access-denied-reason"),
                    "portal", portal, "reason", reason));
        } else {
            player.sendMessage(MessageUtils.parse(
                    plugin.getLangManager().getRaw("portal-access-denied"),
                    "portal", portal));
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getConfigManager().isBossBarShowOnJoin()) {
            plugin.getBossBarManager().showAll(event.getPlayer());
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getBossBarManager().hideAll(event.getPlayer());
    }
    @Nullable
    private PortalType resolveType(@NotNull PlayerPortalEvent event) {
        return switch (event.getCause()) {
            case NETHER_PORTAL -> PortalType.NETHER;
            case END_PORTAL    -> PortalType.END;
            case END_GATEWAY   -> PortalType.GATEWAY;
            default            -> null;
        };
    }
}