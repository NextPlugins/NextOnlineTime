package com.nextplugins.onlinetime.manager;

import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.api.models.comparators.RewardComparator;
import com.nextplugins.onlinetime.parser.RewardParser;
import lombok.Getter;

import java.util.LinkedHashMap;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class RewardManager {

    private final RewardParser rewardParser = new RewardParser();

    @Getter private final LinkedHashMap<String, Reward> rewards = new LinkedHashMap<>();

    public void loadRewards() {

        this.rewardParser.parseFromConfig()
            .stream()
            .sorted(new RewardComparator())
            .forEach(this::addReward);

    }

    public void addReward(Reward reward) {
        this.rewards.put(reward.getName(), reward);
    }

    public Reward getByName(String name) {
        return this.rewards.getOrDefault(name, null);
    }

}
