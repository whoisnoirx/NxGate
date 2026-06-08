package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.models.PortalState;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.MessageUtils;
import com.noirx.nxgate.utils.TimeUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.EnumMap;
import java.util.Map;
public class BossBarManager {
    private final NxGate plugin;
    private final Map<PortalType, BossBar> bars = new EnumMap<>(PortalType.class);
    private final Map<PortalType, Long> barTotals = new EnumMap<>(PortalType.class);
    public BossBarManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    public void startCountdown(@NotNull PortalType type) {
        if (!plugin.getConfigManager().isBossBarEnabled()) return;
        stop(type);
        PortalState state = plugin.getPortalManager().getState(type);
        long remaining = state.getRemainingMillis();
        barTotals.put(type, remaining > 0 ? remaining : 1L);
        BossBar bar = BossBar.bossBar(
                buildTitle(type, remaining),
                1.0f,
                toAdventureColor(plugin.getConfigManager().getBossBarColor(type)),
                toAdventureStyle(plugin.getConfigManager().getBossBarStyle())
        );
        bars.put(type, bar);
        for (Player p : Bukkit.getOnlinePlayers()) p.showBossBar(bar);
    }
    public void stop(@NotNull PortalType type) {
        BossBar bar = bars.remove(type);
        barTotals.remove(type);
        if (bar == null) return;
        for (Player p : Bukkit.getOnlinePlayers()) p.hideBossBar(bar);
    }
    public void tick() {
        for (Map.Entry<PortalType, BossBar> entry : bars.entrySet()) {
            PortalType type  = entry.getKey();
            BossBar bar      = entry.getValue();
            PortalState state = plugin.getPortalManager().getState(type);
            if (!state.isOpen() || !state.hasTimer()) {
                stop(type);
                continue;
            }
            long remaining = state.getRemainingMillis();
            long total     = barTotals.getOrDefault(type, 1L);
            float progress = Math.min(1.0f, Math.max(0.0f, (float) remaining / (float) total));
            bar.name(buildTitle(type, remaining));
            bar.progress(progress);
            bar.color(remaining < 30_000
                    ? BossBar.Color.RED
                    : remaining < 120_000
                    ? BossBar.Color.YELLOW
                    : toAdventureColor(plugin.getConfigManager().getBossBarColor(type)));
        }
    }
    public void refreshCountdown(@NotNull PortalType type) {
        BossBar bar = bars.get(type);
        if (bar == null) return;
        PortalState state = plugin.getPortalManager().getState(type);
        long remaining = state.getRemainingMillis();
        long total     = barTotals.getOrDefault(type, 1L);
        long newTotal  = Math.max(total, remaining);
        barTotals.put(type, newTotal);
        bar.progress(1.0f);
        bar.name(buildTitle(type, remaining));
    }
    public void showAll(@NotNull Player player) {
        for (BossBar bar : bars.values()) player.showBossBar(bar);
    }
    public void hideAll(@NotNull Player player) {
        for (BossBar bar : bars.values()) player.hideBossBar(bar);
    }
    @NotNull
    private Component buildTitle(@NotNull PortalType type, long remainingMillis) {
        String time   = TimeUtils.formatDuration(remainingMillis, true);
        String raw    = plugin.getLangManager().getRawNoPfx("bossbar-text");
        return MessageUtils.parse(raw, "portal", type.getDisplayName(), "time", time,
                "icon", type.getIcon());
    }
    @NotNull
    private BossBar.Color toAdventureColor(@NotNull BarColor color) {
        return switch (color) {
            case BLUE   -> BossBar.Color.BLUE;
            case GREEN  -> BossBar.Color.GREEN;
            case PINK   -> BossBar.Color.PINK;
            case PURPLE -> BossBar.Color.PURPLE;
            case WHITE  -> BossBar.Color.WHITE;
            case YELLOW -> BossBar.Color.YELLOW;
            default     -> BossBar.Color.RED;
        };
    }
    @NotNull
    private BossBar.Overlay toAdventureStyle(@NotNull org.bukkit.boss.BarStyle style) {
        return switch (style) {
            case SEGMENTED_6  -> BossBar.Overlay.NOTCHED_6;
            case SEGMENTED_10 -> BossBar.Overlay.NOTCHED_10;
            case SEGMENTED_12 -> BossBar.Overlay.NOTCHED_12;
            case SEGMENTED_20 -> BossBar.Overlay.NOTCHED_20;
            default           -> BossBar.Overlay.PROGRESS;
        };
    }
}