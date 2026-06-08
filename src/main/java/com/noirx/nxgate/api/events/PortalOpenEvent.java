package com.noirx.nxgate.api.events;
import com.noirx.nxgate.models.PortalType;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class PortalOpenEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final PortalType portal;
    private final CommandSender opener;
    private boolean cancelled;
    public PortalOpenEvent(@NotNull PortalType portal, @Nullable CommandSender opener) {
        this.portal = portal;
        this.opener = opener;
        this.cancelled = false;
    }
    @NotNull public PortalType getPortal()     { return portal; }
    @Nullable public CommandSender getOpener() { return opener; }
    @Override public boolean isCancelled()                 { return cancelled; }
    @Override public void setCancelled(boolean cancel)     { this.cancelled = cancel; }
    @Override public @NotNull HandlerList getHandlers()    { return HANDLERS; }
    public static HandlerList getHandlerList()             { return HANDLERS; }
}