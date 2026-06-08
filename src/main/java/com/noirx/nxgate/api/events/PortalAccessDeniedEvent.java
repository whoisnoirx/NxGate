package com.noirx.nxgate.api.events;
import com.noirx.nxgate.models.PortalType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class PortalAccessDeniedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final PortalType portal;
    private final String reason;
    private boolean cancelled;
    public PortalAccessDeniedEvent(@NotNull Player player, @NotNull PortalType portal,
                                    @Nullable String reason) {
        this.player    = player;
        this.portal    = portal;
        this.reason    = reason;
        this.cancelled = false;
    }
    @NotNull public Player getPlayer()         { return player; }
    @NotNull public PortalType getPortal()     { return portal; }
    @Nullable public String getReason()        { return reason; }
    @Override public boolean isCancelled()              { return cancelled; }
    @Override public void setCancelled(boolean cancel)  { this.cancelled = cancel; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList()          { return HANDLERS; }
}