package com.noirx.nxgate.tests;
import com.noirx.nxgate.models.PortalType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PortalTypeTest {
    @Test
    void testFromIdNether() {
        assertEquals(PortalType.NETHER, PortalType.fromId("nether"));
    }
    @Test
    void testFromIdEnd() {
        assertEquals(PortalType.END, PortalType.fromId("end"));
    }
    @Test
    void testFromIdGateway() {
        assertEquals(PortalType.GATEWAY, PortalType.fromId("gateway"));
    }
    @Test
    void testFromIdCaseInsensitive() {
        assertEquals(PortalType.END, PortalType.fromId("END"));
        assertEquals(PortalType.END, PortalType.fromId("End"));
    }
    @Test
    void testFromIdUnknownReturnsNull() {
        assertNull(PortalType.fromId("unknown"));
        assertNull(PortalType.fromId(""));
        assertNull(PortalType.fromId(null));
    }
    @Test
    void testDisplayNames() {
        assertEquals("The Nether",  PortalType.NETHER.getDisplayName());
        assertEquals("The End",     PortalType.END.getDisplayName());
        assertEquals("End Gateway", PortalType.GATEWAY.getDisplayName());
    }
    @Test
    void testIds() {
        assertEquals("nether",  PortalType.NETHER.getId());
        assertEquals("end",     PortalType.END.getId());
        assertEquals("gateway", PortalType.GATEWAY.getId());
    }
    @Test
    void testAllValuesPresent() {
        assertEquals(3, PortalType.values().length);
    }
}