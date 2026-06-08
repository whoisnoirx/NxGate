package com.noirx.nxgate.tests;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
class PlaceholderExpansionTest {
    @Test
    void testStatusOpenPortal() {
        PortalState state = new PortalState(PortalType.END, true);
        String result = state.isOpen() ? "open" : "closed";
        assertEquals("open", result);
    }
    @Test
    void testStatusClosedPortal() {
        PortalState state = new PortalState(PortalType.NETHER, false);
        String result = state.isOpen() ? "open" : "closed";
        assertEquals("closed", result);
    }
    @Test
    void testStatusColoredOpen() {
        PortalState state = new PortalState(PortalType.END, true);
        String result = state.isOpen() ? "§aOpen" : "§cClosed";
        assertEquals("§aOpen", result);
    }
    @Test
    void testStatusColoredClosed() {
        PortalState state = new PortalState(PortalType.GATEWAY, false);
        String result = state.isOpen() ? "§aOpen" : "§cClosed";
        assertEquals("§cClosed", result);
    }
    @Test
    void testOpenBooleanTrue() {
        PortalState state = new PortalState(PortalType.END, true);
        assertEquals("true", String.valueOf(state.isOpen()));
    }
    @Test
    void testOpenBooleanFalse() {
        PortalState state = new PortalState(PortalType.END, false);
        assertEquals("false", String.valueOf(state.isOpen()));
    }
    @Test
    void testReasonWhenPresent() {
        PortalState state = new PortalState(PortalType.END, false);
        state.setCloseReason("Dragon Respawn");
        String result = state.getCloseReason() != null ? state.getCloseReason() : "";
        assertEquals("Dragon Respawn", result);
    }
    @Test
    void testReasonWhenAbsent() {
        PortalState state = new PortalState(PortalType.END, true);
        String result = state.getCloseReason() != null ? state.getCloseReason() : "";
        assertEquals("", result);
    }
    @Test
    void testTimeRemainingWhenNoTimer() {
        PortalState state = new PortalState(PortalType.END, true);
        String result = (!state.isOpen() || !state.hasTimer()) ? "-"
                : TimeUtils.formatDuration(state.getRemainingMillis(), true);
        assertEquals("-", result);
    }
    @Test
    void testTimeRemainingWhenTimerActive() {
        PortalState state = new PortalState(PortalType.END, true);
        state.setAutoCloseAt(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));
        assertTrue(state.hasTimer());
        long remaining = state.getRemainingMillis();
        assertTrue(remaining > 0);
        String formatted = TimeUtils.formatDuration(remaining, true);
        assertNotNull(formatted);
        assertNotEquals("-", formatted);
    }
    @Test
    void testRemainingSecondsNoTimer() {
        PortalState state = new PortalState(PortalType.NETHER, true);
        String result = (!state.isOpen() || !state.hasTimer()) ? "-1"
                : String.valueOf(Math.max(0, state.getRemainingMillis() / 1000));
        assertEquals("-1", result);
    }
    @Test
    void testRemainingSecondsWithTimer() {
        PortalState state = new PortalState(PortalType.NETHER, true);
        state.setAutoCloseAt(System.currentTimeMillis() + 60_000L);
        long seconds = Math.max(0, state.getRemainingMillis() / 1000);
        assertTrue(seconds > 0 && seconds <= 60);
    }
    @Test
    void testFromIdUsedByPlaceholderParser() {
        assertNotNull(PortalType.fromId("nether"));
        assertNotNull(PortalType.fromId("end"));
        assertNotNull(PortalType.fromId("gateway"));
        assertNull(PortalType.fromId("invalid_portal"));
    }
}