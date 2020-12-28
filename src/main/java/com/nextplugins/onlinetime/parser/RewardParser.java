package com.nextplugins.onlinetime.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.utils.ColorUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class RewardParser {

    @Inject @Named("main") private Logger logger;
    @Inject @Named("rewards") private Configuration configuration;
    @Inject private ItemParser itemParser;

    public Reward parseSection(ConfigurationSection section) {

        try {

            return Reward.builder()
                    .name(section.getName())
                    .coloredName(ColorUtils.colored(section.getString("name")))
                    .icon(this.itemParser.parseSection(section))
                    .time(TimeUnit.MINUTES.toMillis(section.getInt("time")))
                    .description(ColorUtils.colored(section.getStringList("description")))
                    .commands(section.getStringList("commands"))
                    .build();

        } catch (Exception exception) {

            this.logger.warning("Error on parse reward " + section.getName());
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

        return this.parseListSection(
                configuration.getConfigurationSection("")
        );

    }

}
