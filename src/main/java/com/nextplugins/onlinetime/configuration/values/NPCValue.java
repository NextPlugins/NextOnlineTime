package com.nextplugins.onlinetime.configuration.values;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.utils.LocationUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NPCValue {

    private static final NPCValue instance = new NPCValue();

    private final Configuration configuration = NextOnlineTime.getInstance().getNpcConfig();

    private final List<String> hologramMessage = messageList("hologram.message");

    private final String skinNick = configuration.getString("skin-nick");
    private final String npcName = message("name");

    private final double heightToAdd = configuration.getDouble("hologram.height");

    private final boolean lookCLose = configuration.getBoolean("lookclose");

    private final Location position = LocationUtils.deserialize(configuration.getString("position"));

    public static <T> T get(Function<NPCValue, T> supplier) {
        return supplier.apply(NPCValue.instance);
    }

    private String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String message(String key) {
        return colored(configuration.getString(key));
    }

    private List<String> messageList(String key) {
        return configuration.getStringList(key)
            .stream()
            .map(this::colored)
            .collect(Collectors.toList());
    }

}
