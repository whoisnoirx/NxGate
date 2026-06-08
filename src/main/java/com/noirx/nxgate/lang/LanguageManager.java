package com.noirx.nxgate.lang;
import com.noirx.nxgate.NxGate;
import com.noirx.nxgate.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
public class LanguageManager {
    private final NxGate plugin;
    private YamlConfiguration active;
    private YamlConfiguration fallback;
    public LanguageManager(@NotNull NxGate plugin) {
        this.plugin = plugin;
        reload();
    }
    public void reload() {
        String lang = plugin.getConfigManager().getLanguage();
        active   = loadLang(lang);
        fallback = loadLang("en_US");
    }
    @NotNull
    public String getRaw(@NotNull String key) {
        String prefix = getString("prefix");
        String value  = getString(key);
        if (value.isEmpty()) return "";
        return prefix + value;
    }
    @NotNull
    public String getRawNoPfx(@NotNull String key) {
        return getString(key);
    }
    @NotNull
    public Component get(@NotNull String key, @NotNull String... keyValues) {
        return MessageUtils.parse(getRaw(key), keyValues);
    }
    @NotNull
    public Component getNoPfx(@NotNull String key, @NotNull String... keyValues) {
        return MessageUtils.parse(getRawNoPfx(key), keyValues);
    }
    @NotNull
    private String getString(@NotNull String key) {
        if (active != null && active.contains(key)) return active.getString(key, "");
        if (fallback != null) return fallback.getString(key, "");
        return "";
    }
    @Nullable
    private YamlConfiguration loadLang(@NotNull String locale) {
        String resourcePath = "lang/" + locale + ".yml";
        File langFile = new File(plugin.getDataFolder(), resourcePath);
        if (!langFile.exists()) {
            try { plugin.saveResource(resourcePath, false); }
            catch (IllegalArgumentException e) {
                plugin.getLogger().warning("No bundled language file for: " + locale);
            }
        }
        if (langFile.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
                return YamlConfiguration.loadConfiguration(reader);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load language file: " + resourcePath, e);
            }
        }
        InputStream is = plugin.getResource(resourcePath);
        if (is != null) {
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return YamlConfiguration.loadConfiguration(reader);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load bundled language: " + resourcePath, e);
            }
        }
        return null;
    }
}