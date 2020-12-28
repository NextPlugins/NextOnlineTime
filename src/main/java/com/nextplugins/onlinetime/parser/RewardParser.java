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

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class RewardParser {

    @Inject @Named("rewards") private Configuration configuration;
    @Inject private ItemParser itemParser;

    public Reward parseSection(ConfigurationSection section) {

        return Reward.builder()
                .name(section.getName())
                .coloredName(ColorUtils.colored(section.getString("name")))
                .icon(this.itemParser.parseSection(section))
                .time(TimeUnit.MINUTES.toMillis(section.getInt("time")))
                .description(ColorUtils.colored(section.getStringList("description")))
                .commands(section.getStringList("commands"))
                .build();

    }

    public List<Reward> parseListSection(ConfigurationSection section) {

        List<Reward> rewards = new ArrayList<>();
        for (String key : section.getKeys(false)) {

            rewards.add(
                    this.parseSection(section.getConfigurationSection(key))
            );

        }

        return rewards;

    }

    public List<Reward> parseFromConfig() {

        return this.parseListSection(
                configuration.getConfigurationSection("")
        );

    }

}
