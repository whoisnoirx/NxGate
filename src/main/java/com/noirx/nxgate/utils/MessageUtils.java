package com.noirx.nxgate.utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
public final class MessageUtils {
    private MessageUtils() {}
    private static final MiniMessage MM = MiniMessage.miniMessage();
    @NotNull
    public static Component parse(@NotNull String raw, @NotNull String... keyValues) {
        return MM.deserialize(replacePlaceholders(raw, keyValues));
    }
    @NotNull
    public static Component parse(@NotNull String raw) {
        return MM.deserialize(raw);
    }
    public static void send(@NotNull CommandSender sender, @NotNull String raw,
                             @NotNull String... keyValues) {
        sender.sendMessage(parse(raw, keyValues));
    }
    public static void broadcast(@NotNull Collection<? extends CommandSender> recipients,
                                  @NotNull String raw, @NotNull String... keyValues) {
        Component component = parse(raw, keyValues);
        for (CommandSender r : recipients) r.sendMessage(component);
    }
    @NotNull
    public static String stripTags(@NotNull String raw) {
        return MM.stripTags(raw);
    }
    @NotNull
    public static String replacePlaceholders(@NotNull String raw, @NotNull String... keyValues) {
        String result = raw;
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            String key   = keyValues[i];
            String value = keyValues[i + 1];
            if (key != null && value != null) {
                result = result.replace("%" + key + "%", value);
            }
        }
        return result;
    }
}