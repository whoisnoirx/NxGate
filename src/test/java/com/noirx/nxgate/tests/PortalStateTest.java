package com.noirx.nxgate.tests;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PortalStateTest {
    private PortalState state;
    @BeforeEach
    void setUp() {
        state = new PortalState(PortalType.END, true);
    }
    @Test
    void testInitiallyOpen() {
        assertTrue(state.isOpen());
    }
    @Test
    void testSetClosed() {
        state.setOpen(false);
        assertFalse(state.isOpen());
    }
    @Test
    void testNoTimer() {
        assertFalse(state.hasTimer());
        assertEquals(-1L, state.getAutoCloseAt());
        assertEquals(-1L, state.getRemainingMillis());
    }
    @Test
    void testSetTimer() {
        long future = System.currentTimeMillis() + 60_000L;
        state.setAutoCloseAt(future);
        assertTrue(state.hasTimer());
        assertTrue(state.getRemainingMillis() > 0);
        assertTrue(state.getRemainingMillis() <= 60_000L);
    }
    @Test
    void testClearTimer() {
        state.setAutoCloseAt(System.currentTimeMillis() + 60_000L);
        assertTrue(state.hasTimer());
        state.clearTimer();
        assertFalse(state.hasTimer());
    }
    @Test
    void testExpiredTimer() {
        state.setAutoCloseAt(System.currentTimeMillis() - 1000L);
        assertTrue(state.hasTimer());
        assertEquals(0L, state.getRemainingMillis());
    }
    @Test
    void testCloseReason() {
        assertNull(state.getCloseReason());
        state.setCloseReason("Dragon Respawn");
        assertEquals("Dragon Respawn", state.getCloseReason());
        state.setCloseReason(null);
        assertNull(state.getCloseReason());
    }
    @Test
    void testPortalType() {
        assertEquals(PortalType.END, state.getType());
    }
}