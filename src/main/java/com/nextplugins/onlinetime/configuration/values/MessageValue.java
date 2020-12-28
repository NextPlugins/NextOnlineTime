package com.nextplugins.onlinetime.configuration.values;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import javax.inject.Named;
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
public class MessageValue {

    @Getter private static final MessageValue instance = new MessageValue();
    @Inject @Named("messages") private Configuration config;

    private final List<String> helpMessage = messageList("command-help");
    private final List<String> rewardLore = messageList("rewardInfo.reward-lore");

    private final String timeOfTarget = message("time-of-target");
    private final String collectedReward = message("collected-reward");

    private final String alreadyCollected = message("rewardInfo.already-collected");
    private final String noTimeToCollect = message("rewardInfo.no-time-to-collect");
    private final String collect = message("rewardInfo.collect");

    public static <T> T get(Function<MessageValue, T> supplier) {
        return supplier.apply(MessageValue.instance);
    }

    private String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String message(String key) {
        return colored(config.getString(key));
    }

    private List<String> messageList(String key) {
        return config.getStringList(key)
                .stream()
                .map(this::colored)
                .collect(Collectors.toList());
    }

}
