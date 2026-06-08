package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
public class SoundManager {
    private final NxGate plugin;
    public SoundManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    public void playOpenSound() {
        if (!plugin.getConfigManager().isSoundsEnabled()) return;
        var sound  = plugin.getConfigManager().getOpenSound();
        var volume = plugin.getConfigManager().getOpenVolume();
        var pitch  = plugin.getConfigManager().getOpenPitch();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }
    public void playCloseSound() {
        if (!plugin.getConfigManager().isSoundsEnabled()) return;
        var sound  = plugin.getConfigManager().getCloseSound();
        var volume = plugin.getConfigManager().getCloseVolume();
        var pitch  = plugin.getConfigManager().getClosePitch();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }
}