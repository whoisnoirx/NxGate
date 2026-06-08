package com.noirx.nxgate.api;
import com.noirx.nxgate.managers.PortalManager;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class NxGateAPI {
    private static NxGateAPI instance;
    private final PortalManager portalManager;
    public NxGateAPI(@NotNull PortalManager portalManager) {
        this.portalManager = portalManager;
        instance = this;
    }
    @NotNull
    public static NxGateAPI getInstance() {
        if (instance == null) throw new IllegalStateException("NxGate is not loaded yet.");
        return instance;
    }
    public boolean isPortalOpen(@NotNull PortalType type) {
        return portalManager.isOpen(type);
    }
    public void openPortal(@NotNull PortalType type, @Nullable CommandSender opener) {
        portalManager.openPortal(type, opener, null);
    }
    public void openPortal(@NotNull PortalType type, @Nullable CommandSender opener, long durationMillis) {
        portalManager.openPortal(type, opener, durationMillis > 0 ? durationMillis : null);
    }
    public void closePortal(@NotNull PortalType type, @Nullable CommandSender closer, @Nullable String reason) {
        portalManager.closePortal(type, closer, reason);
    }
    @NotNull
    public PortalState getPortalState(@NotNull PortalType type) {
        return portalManager.getState(type);
    }
}