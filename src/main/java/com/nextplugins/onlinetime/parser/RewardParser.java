package com.nextplugins.onlinetime.parser;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.utils.ColorUtils;
import lombok.val;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class RewardParser {

    private final Configuration configuration = NextOnlineTime.getInstance().getRewardsConfig();
    private final ItemParser itemParser = new ItemParser();

    public Reward parseSection(ConfigurationSection section) {

        try {

            val icon = this.itemParser.parseSection(section);
            if (icon == null) return null;

            return Reward.builder()
                .name(section.getName())
                .coloredName(ColorUtils.colored(section.getString("name")))
                .icon(icon)
                .time(TimeUnit.MINUTES.toMillis(section.getInt("time")))
                .description(ColorUtils.colored(section.getStringList("description")))
                .commands(section.getStringList("commands"))
                .build();

        } catch (Exception exception) {

            NextOnlineTime.getInstance().getLogger().warning("Error on parse reward " + section.getName());
            return null;

        }

    }

    public List<Reward> parseListSection(ConfigurationSection section) {

        List<Reward> rewards = new ArrayList<>();
        for (String key : section.getKeys(false)) {

            Reward reward = this.parseSection(section.getConfigurationSection(key));
            if (reward == null) continue;

            rewards.add(reward);

        }

        return rewards;

    }

    public List<Reward> parseFromConfig() {
        return parseListSection(configuration.getConfigurationSection(""));
    }

}
