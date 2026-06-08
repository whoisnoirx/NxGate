package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.api.events.PortalCloseEvent;
import com.noirx.nxgate.api.events.PortalOpenEvent;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.FoliaUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;
public class PortalManager {
    private final NxGate plugin;
    private final Map<PortalType, PortalState> states = new EnumMap<>(PortalType.class);
    public PortalManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
        for (PortalType type : PortalType.values()) {
            boolean defaultOpen = plugin.getConfigManager().isPortalDefaultOpen(type);
            states.put(type, new PortalState(type, defaultOpen));
        }
    }
    @NotNull
    public PortalState getState(@NotNull PortalType type) {
        return states.get(type);
    }
    public boolean isOpen(@NotNull PortalType type) {
        return states.get(type).isOpen();
    }
    public boolean openPortal(@NotNull PortalType type, @Nullable CommandSender opener,
                               @Nullable Long durationMillis) {
        PortalState state = states.get(type);
        if (state.isOpen() && durationMillis == null) return false;
        PortalOpenEvent event = new PortalOpenEvent(type, opener);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        state.setOpen(true);
        state.setCloseReason(null);
        if (durationMillis != null && durationMillis > 0) {
            state.setAutoCloseAt(System.currentTimeMillis() + durationMillis);
        } else {
            state.clearTimer();
        }
        plugin.getBroadcastManager().broadcastOpen(type, opener);
        plugin.getSoundManager().playOpenSound();
        if (state.hasTimer()) {
            plugin.getBossBarManager().startCountdown(type);
        } else {
            plugin.getBossBarManager().stop(type);
        }
        return true;
    }
    public boolean closePortal(@NotNull PortalType type, @Nullable CommandSender closer,
                                @Nullable String reason) {
        PortalState state = states.get(type);
        if (!state.isOpen()) return false;
        PortalCloseEvent event = new PortalCloseEvent(type, closer, reason);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        String finalReason = event.getReason();
        state.setOpen(false);
        state.setCloseReason(finalReason);
        state.clearTimer();
        plugin.getBroadcastManager().broadcastClose(type, closer, finalReason);
        plugin.getSoundManager().playCloseSound();
        plugin.getBossBarManager().stop(type);
        plugin.getPlayerManager().handleDimensionClose(type);
        return true;
    }
    public boolean extendTimer(@NotNull PortalType type, long extraMillis) {
        PortalState state = states.get(type);
        if (!state.isOpen()) return false;
        if (!state.hasTimer()) return false;
        long newDeadline = state.getAutoCloseAt() + extraMillis;
        state.setAutoCloseAt(newDeadline);
        plugin.getBossBarManager().refreshCountdown(type);
        return true;
    }
    public void tickAutoClose() {
        for (PortalState state : states.values()) {
            if (state.isOpen() && state.hasTimer() && state.getRemainingMillis() <= 0) {
                closePortal(state.getType(), null, null);
            }
        }
    }
}