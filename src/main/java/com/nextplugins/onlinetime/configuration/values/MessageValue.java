package com.nextplugins.onlinetime.configuration.values;

import com.nextplugins.onlinetime.NextOnlineTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
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
public final class MessageValue {

    private static final MessageValue instance = new MessageValue();

    private final Configuration configuration = NextOnlineTime.getInstance().getMessagesConfig();

    private final List<String> helpMessage = messageList("command-help");
    private final List<String> rewardLore = messageList("rewardInfo.reward-lore");

    private final String incorrectUsage = message("incorrect-usage");

    private final String cantSendForYou = message("cant-send-for-you");
    private final String timeOfTarget = message("time-of-target");
    private final String usedTime = message("used-time");
    private final String collectedReward = message("collected-reward");
    private final String noSpace = message("no-space");

    private final String invalidTime = message("invalid-time");
    private final String noTime = message("no-time");

    private final String sendedTime = message("sendTime.sended");
    private final String receivedTime = message("sendTime.received");

    private final String alreadyCollected = message("rewardInfo.already-collected");
    private final String noTimeToCollect = message("rewardInfo.no-time-collect");
    private final String canCollect = message("rewardInfo.canCollect");

    public static <T> T get(Function<MessageValue, T> supplier) {
        return supplier.apply(MessageValue.instance);
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
