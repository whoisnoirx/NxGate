package com.noirx.nxgate.models;
import org.jetbrains.annotations.Nullable;
public class PortalState {
    private final PortalType type;
    private boolean open;
    @Nullable private String closeReason;
    private long autoCloseAt;
    public PortalState(PortalType type, boolean open) {
        this.type = type;
        this.open = open;
        this.closeReason = null;
        this.autoCloseAt = -1L;
    }
    public PortalType getType()                            { return type; }
    public boolean isOpen()                                { return open; }
    public void setOpen(boolean open)                      { this.open = open; }
    @Nullable public String getCloseReason()               { return closeReason; }
    public void setCloseReason(@Nullable String reason)    { this.closeReason = reason; }
    public boolean hasTimer()                              { return autoCloseAt > 0; }
    public long getAutoCloseAt()                           { return autoCloseAt; }
    public void setAutoCloseAt(long autoCloseAt)           { this.autoCloseAt = autoCloseAt; }
    public void clearTimer()                               { this.autoCloseAt = -1L; }
    public long getRemainingMillis() {
        if (!hasTimer()) return -1L;
        return Math.max(0, autoCloseAt - System.currentTimeMillis());
    }
}