package com.noirx.nxgate.managers;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.config.ConfigManager;
import com.noirx.nxgate.models.PortalType;
import com.noirx.nxgate.utils.FoliaUtils;
import com.noirx.nxgate.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class PlayerManager {
    private final NxGate plugin;
    private final Map<UUID, Location> entryPoints = new ConcurrentHashMap<>();
    public PlayerManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
    }
    public void saveEntryPoint(@NotNull UUID uuid, @NotNull Location location) {
        entryPoints.put(uuid, location.clone());
    }
    public void clearEntryPoint(@NotNull UUID uuid) {
        entryPoints.remove(uuid);
    }
    public void handleDimensionClose(@NotNull PortalType type) {
        if (!plugin.getConfigManager().isKickPlayersEnabled()) return;
        List<Player> targets = getPlayersInDimension(type);
        if (targets.isEmpty()) return;
        ConfigManager.KickMode mode = plugin.getConfigManager().getKickMode();
        String portal = type.getDisplayName();
        int delay = plugin.getConfigManager().getTeleportDelay();
        for (Player p : targets) {
            if (p.hasPermission("nxgate.bypass")) continue;
            if (mode == ConfigManager.KickMode.KICK) {
                String raw = plugin.getLangManager().getRaw("player-kicked");
                String plain = MessageUtils.stripTags(
                        MessageUtils.replacePlaceholders(raw, "portal", portal));
                FoliaUtils.runSync(plugin, () -> p.kickPlayer(plain));
            } else {
                Location dest = resolveDestination(p);
                if (dest != null) {
                    if (delay > 0) {
                        FoliaUtils.runLater(plugin, () -> FoliaUtils.teleport(plugin, p, dest), delay);
                    } else {
                        FoliaUtils.teleport(plugin, p, dest);
                    }
                }
                FoliaUtils.runSync(plugin, () -> {
                    String raw = plugin.getLangManager().getRaw("player-removed");
                    p.sendMessage(MessageUtils.parse(raw, "portal", portal));
                });
            }
        }
    }
    @NotNull
    private List<Player> getPlayersInDimension(@NotNull PortalType type) {
        List<Player> result = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getEnvironment() == type.getEnvironment()) result.add(p);
        }
        return result;
    }
    @Nullable
    private Location resolveDestination(@NotNull Player player) {
        ConfigManager.TeleportDestination dest = plugin.getConfigManager().getTeleportDestination();
        return switch (dest) {
            case ENTRY -> {
                Location saved = entryPoints.get(player.getUniqueId());
                yield saved != null ? saved.clone() : getWorldSpawn();
            }
            case HOME -> {
                Location home = resolveEssentialsHome(player);
                if (home != null) yield home;
                yield resolveFallback(plugin.getConfigManager().getHomeFallback(), player);
            }
            case SPAWN -> {
                Location bed = player.getBedSpawnLocation();
                yield bed != null ? bed : getWorldSpawn();
            }
            case WORLD_SPAWN -> getWorldSpawn();
            case LOCATION -> {
                Location loc = plugin.getConfigManager().getTeleportLocation();
                yield loc != null ? loc : getWorldSpawn();
            }
        };
    }
    @Nullable
    private Location resolveFallback(@NotNull ConfigManager.TeleportDestination fallback,
                                      @NotNull Player player) {
        return switch (fallback) {
            case SPAWN -> {
                Location bed = player.getBedSpawnLocation();
                yield bed != null ? bed : getWorldSpawn();
            }
            case LOCATION -> {
                Location loc = plugin.getConfigManager().getTeleportLocation();
                yield loc != null ? loc : getWorldSpawn();
            }
            default -> getWorldSpawn();
        };
    }
    @Nullable
    private Location resolveEssentialsHome(@NotNull Player player) {
        try {
            Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
            if (ess == null) ess = Bukkit.getPluginManager().getPlugin("EssentialsX");
            if (ess == null || !ess.isEnabled()) return null;
            Object user = ess.getClass().getMethod("getUser", Player.class).invoke(ess, player);
            if (user == null) return null;
            Class<?> userClass = user.getClass();
            boolean hasHome;
            try {
                hasHome = (boolean) userClass.getMethod("hasHome", String.class).invoke(user, "home");
            } catch (NoSuchMethodException e) {
                Collection<?> homes = (Collection<?>) userClass.getMethod("getHomes").invoke(user);
                hasHome = homes != null && !homes.isEmpty();
                if (!hasHome) return null;
                Object homeName = homes.iterator().next();
                return (Location) userClass.getMethod("getHome", String.class).invoke(user, homeName.toString());
            }
            if (!hasHome) return null;
            return (Location) userClass.getMethod("getHome", String.class).invoke(user, "home");
        } catch (Exception ignored) {
            return null;
        }
    }
    @Nullable
    private Location getWorldSpawn() {
        World world = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst().orElse(null);
        return world != null ? world.getSpawnLocation() : null;
    }
}