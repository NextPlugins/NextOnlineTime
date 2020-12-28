package com.nextplugins.onlinetime.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.parser.RewardParser;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class RewardManager {

    @Inject private RewardParser rewardParser;

    @Getter private final Map<String, Reward> rewards = new HashMap<>();

    public void loadRewards() {
        this.rewardParser.parseFromConfig().forEach(this::addReward);
    }

    public void addReward(Reward reward) {
        this.rewards.put(reward.getName(), reward);
    }

    public Reward getByName(String name) {
        return this.rewards.getOrDefault(name, null);
    }

}
