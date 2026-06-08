package com.noirx.nxgate;
import com.noirx.nxgate.api.NxGateAPI;
import com.noirx.nxgate.commands.NxGateCommand;
import com.noirx.nxgate.config.ConfigManager;
import com.noirx.nxgate.lang.LanguageManager;
import com.noirx.nxgate.listeners.PortalListener;
import com.noirx.nxgate.managers.*;
import com.noirx.nxgate.placeholders.NxGatePlaceholderExpansion;
import com.noirx.nxgate.scheduler.NxGateScheduler;
import com.noirx.nxgate.utils.FoliaUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
public final class NxGate extends JavaPlugin {
    public static final String AUTHOR  = "whoisnoirx";
    public static final String CONTACT = "whois.noirx";
    private static final int BSTATS_ID = 31872;
    private ConfigManager    configManager;
    private LanguageManager  langManager;
    private PortalManager    portalManager;
    private BroadcastManager broadcastManager;
    private SoundManager     soundManager;
    private BossBarManager   bossBarManager;
    private PlayerManager    playerManager;
    private ScheduleManager  scheduleManager;
    private NxGateAPI        api;
    @Override
    public void onEnable() {
        printBanner();
        this.configManager    = new ConfigManager(this);
        this.langManager      = new LanguageManager(this);
        this.broadcastManager = new BroadcastManager(this);
        this.soundManager     = new SoundManager(this);
        this.bossBarManager   = new BossBarManager(this);
        this.playerManager    = new PlayerManager(this);
        this.portalManager    = new PortalManager(this);
        this.scheduleManager  = new ScheduleManager(this);
        new NxGateScheduler(this).start();
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NxGatePlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI hooked — %nxgate_...% placeholders active.");
        }
        this.api = new NxGateAPI(portalManager);
        NxGateCommand cmd = new NxGateCommand(this);
        var reg = getCommand("nxgate");
        if (reg != null) {
            reg.setExecutor(cmd);
            reg.setTabCompleter(cmd);
        }
        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        if (configManager.isBStatsEnabled()) new Metrics(this, BSTATS_ID);
        if (configManager.isUpdateCheckerEnabled()) runUpdateCheck();
        getLogger().info("NxGate " + getDescription().getVersion()
                + " ready | " + (FoliaUtils.isFolia() ? "Folia" : "Paper/Spigot")
                + " | by " + AUTHOR);
    }
    @Override
    public void onDisable() {
        getLogger().info("NxGate disabled.");
    }
    @NotNull public ConfigManager    getConfigManager()    { return configManager; }
    @NotNull public LanguageManager  getLangManager()      { return langManager; }
    @NotNull public PortalManager    getPortalManager()    { return portalManager; }
    @NotNull public BroadcastManager getBroadcastManager() { return broadcastManager; }
    @NotNull public SoundManager     getSoundManager()     { return soundManager; }
    @NotNull public BossBarManager   getBossBarManager()   { return bossBarManager; }
    @NotNull public PlayerManager    getPlayerManager()    { return playerManager; }
    @NotNull public ScheduleManager  getScheduleManager()  { return scheduleManager; }
    @NotNull public NxGateAPI        getApi()              { return api; }
    private void printBanner() {
        getLogger().info(" ");
        getLogger().info("  \u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557");
        getLogger().info("  \u2551   N x G a t e  v" + getDescription().getVersion() + "   \u2551");
        getLogger().info("  \u2551   by " + AUTHOR + "   \u2551");
        getLogger().info("  \u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D");
        getLogger().info(" ");
    }
    private void runUpdateCheck() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                var url  = new java.net.URL("https://api.github.com/repos/whoisnoirx/NxGate/releases/latest");
                var conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("User-Agent", "NxGate/" + getDescription().getVersion());
                if (conn.getResponseCode() == 200) {
                    var reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                    var sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();
                    String body = sb.toString();
                    int idx = body.indexOf("\"tag_name\":");
                    if (idx >= 0) {
                        String after = body.substring(idx + 12);
                        String tag = after.substring(0, after.indexOf("\"")).replace("v", "");
                        if (!tag.equals(getDescription().getVersion())) {
                            getLogger().info("[Update] New version available: " + tag
                                    + " — https://modrinth.com/plugin/nxgate");
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception ignored) {}
        });
    }
}