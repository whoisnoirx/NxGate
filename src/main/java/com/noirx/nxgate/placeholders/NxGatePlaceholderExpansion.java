package com.noirx.nxgate.placeholders;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.TimeUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class NxGatePlaceholderExpansion extends PlaceholderExpansion {
    private final NxGate plugin;
    public NxGatePlaceholderExpansion(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    @Override public @NotNull String getIdentifier() { return "nxgate"; }
    @Override public @NotNull String getAuthor()     { return NxGate.AUTHOR; }
    @Override public @NotNull String getVersion()    { return plugin.getDescription().getVersion(); }
    @Override public boolean persist()               { return true; }
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("status_all")) return buildStatusAll();
        String[] parts = params.split("_", 2);
        if (parts.length < 2) return null;
        PortalType type = PortalType.fromId(parts[0]);
        if (type == null) return null;
        String key         = parts[1].toLowerCase();
        PortalState state  = plugin.getPortalManager().getState(type);
        return switch (key) {
            case "status"                 -> state.isOpen() ? "open" : "closed";
            case "status_colored"         -> state.isOpen() ? "\u00a7aOpen" : "\u00a7cClosed";
            case "open"                   -> String.valueOf(state.isOpen());
            case "reason"                 -> state.getCloseReason() != null ? state.getCloseReason() : "";
            case "time_remaining"         -> formatRemaining(state);
            case "time_remaining_seconds" -> formatRemainingSeconds(state);
            case "next_opening"           -> nextOpening(type);
            case "next_remaining"         -> nextRemaining(type);
            default                       -> null;
        };
    }
    @NotNull
    private String buildStatusAll() {
        StringBuilder sb = new StringBuilder();
        for (PortalType type : PortalType.values()) {
            if (sb.length() > 0) sb.append(", ");
            PortalState state = plugin.getPortalManager().getState(type);
            sb.append(type.getDisplayName()).append(": ").append(state.isOpen() ? "Open" : "Closed");
        }
        return sb.toString();
    }
    @NotNull
    private String formatRemaining(@NotNull PortalState state) {
        if (!state.isOpen() || !state.hasTimer()) return "-";
        long remaining = state.getRemainingMillis();
        return remaining > 0 ? TimeUtils.formatDuration(remaining, true) : "Closing...";
    }
    @NotNull
    private String formatRemainingSeconds(@NotNull PortalState state) {
        if (!state.isOpen() || !state.hasTimer()) return "-1";
        return String.valueOf(Math.max(0, state.getRemainingMillis() / 1000));
    }
    @NotNull
    private String nextOpening(@NotNull PortalType type) {
        if (plugin.getPortalManager().isOpen(type)) return "Now";
        String info = plugin.getScheduleManager().getNextOpeningInfo(type);
        if (info == null) return "-";
        return info.split("\\|")[0].trim();
    }
    @NotNull
    private String nextRemaining(@NotNull PortalType type) {
        if (plugin.getPortalManager().isOpen(type)) return "-";
        String info = plugin.getScheduleManager().getNextOpeningInfo(type);
        if (info == null) return "-";
        String[] parts = info.split("\\|", 2);
        return parts.length > 1 ? parts[1].trim() : "-";
    }
}