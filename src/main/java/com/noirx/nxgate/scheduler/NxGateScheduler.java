package com.noirx.nxgate.scheduler;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.utils.FoliaUtils;
import org.jetbrains.annotations.NotNull;
public class NxGateScheduler {
    private final NxGate plugin;
    public NxGateScheduler(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    public void start() {
        FoliaUtils.runRepeating(plugin, () -> {
            plugin.getPortalManager().tickAutoClose();
            plugin.getBossBarManager().tick();
        }, 20L, 20L);
        FoliaUtils.runRepeating(plugin, () ->
                plugin.getScheduleManager().tick(),
                60L, 1200L);
    }
}