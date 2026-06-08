package com.noirx.nxgate.api.events;
import com.noirx.nxgate.models.PortalType;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class PortalCloseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PortalType portal;
    private final CommandSender closer;
    private String reason;
    private boolean cancelled;
    public PortalCloseEvent(@NotNull PortalType portal, @Nullable CommandSender closer,
                             @Nullable String reason) {
        this.portal = portal;
        this.closer = closer;
        this.reason = reason;
        this.cancelled = false;
    }
    @NotNull public PortalType getPortal()      { return portal; }
    @Nullable public CommandSender getCloser()  { return closer; }
    @Nullable public String getReason()         { return reason; }
    public void setReason(@Nullable String r)   { this.reason = r; }
    @Override public boolean isCancelled()              { return cancelled; }
    @Override public void setCancelled(boolean cancel)  { this.cancelled = cancel; }
    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList()          { return HANDLERS; }
}