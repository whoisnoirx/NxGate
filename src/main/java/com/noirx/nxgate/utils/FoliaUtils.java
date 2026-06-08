package com.noirx.nxgate.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
public final class FoliaUtils {
    private FoliaUtils() {}
    private static final boolean FOLIA;
    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;
    }
    public static boolean isFolia() { return FOLIA; }
    public static void runSync(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }
    public static void runLater(@NotNull Plugin plugin, @NotNull Runnable runnable, long delayTicks) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        }
    }
    public static void runRepeating(@NotNull Plugin plugin, @NotNull Runnable runnable,
                                     long initialDelay, long periodTicks) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin,
                    task -> runnable.run(), initialDelay, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelay, periodTicks);
        }
    }
    public static void runAsync(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        if (FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }
    public static void teleport(@NotNull Plugin plugin, @NotNull Entity entity,
                                 @NotNull Location location) {
        if (FOLIA) {
            entity.getScheduler().run(plugin, task -> entity.teleport(location), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> entity.teleport(location));
        }
    }
}