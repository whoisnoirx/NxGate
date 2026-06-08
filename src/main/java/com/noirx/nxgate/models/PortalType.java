package com.noirx.nxgate.models;
import org.bukkit.World;
public enum PortalType {
    NETHER("nether", "The Nether", "\uD83D\uDD25", World.Environment.NETHER),
    END("end", "The End", "\u2B50", World.Environment.THE_END),
    GATEWAY("gateway", "End Gateway", "\u27A1", World.Environment.THE_END);
    private final String id;
    private final String displayName;
    private final String icon;
    private final World.Environment environment;
    PortalType(String id, String displayName, String icon, World.Environment environment) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.environment = environment;
    }
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public String getIconName() { return icon + " " + displayName; }
    public World.Environment getEnvironment() { return environment; }
    public static PortalType fromId(String id) {
        if (id == null) return null;
        for (PortalType type : values()) {
            if (type.id.equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}