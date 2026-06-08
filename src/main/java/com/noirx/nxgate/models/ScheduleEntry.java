package com.noirx.nxgate.models;
import org.jetbrains.annotations.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
public class ScheduleEntry {
    public enum RepeatMode { DAILY, WEEKLY, MONTHLY, ONE_TIME }
    private final PortalType portal;
    private final RepeatMode repeatMode;
    private final Set<DayOfWeek> days;
    @Nullable private final LocalTime openTime;
    @Nullable private final LocalTime closeTime;
    @Nullable private final LocalDateTime openDateTime;
    @Nullable private final LocalDateTime closeDateTime;
    public ScheduleEntry(PortalType portal, Set<DayOfWeek> days, LocalTime openTime, LocalTime closeTime) {
        this.portal = portal;
        this.repeatMode = RepeatMode.WEEKLY;
        this.days = EnumSet.copyOf(days);
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.openDateTime = null;
        this.closeDateTime = null;
    }
    public ScheduleEntry(PortalType portal, LocalTime openTime, LocalTime closeTime) {
        this.portal = portal;
        this.repeatMode = RepeatMode.DAILY;
        this.days = EnumSet.allOf(DayOfWeek.class);
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.openDateTime = null;
        this.closeDateTime = null;
    }
    public ScheduleEntry(PortalType portal, LocalDateTime openDateTime, LocalDateTime closeDateTime) {
        this.portal = portal;
        this.repeatMode = RepeatMode.ONE_TIME;
        this.days = Collections.emptySet();
        this.openTime = null;
        this.closeTime = null;
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
    }
    public PortalType getPortal()                          { return portal; }
    public RepeatMode getRepeatMode()                      { return repeatMode; }
    public Set<DayOfWeek> getDays()                        { return Collections.unmodifiableSet(days); }
    @Nullable public LocalTime getOpenTime()               { return openTime; }
    @Nullable public LocalTime getCloseTime()              { return closeTime; }
    @Nullable public LocalDateTime getOpenDateTime()       { return openDateTime; }
    @Nullable public LocalDateTime getCloseDateTime()      { return closeDateTime; }
}