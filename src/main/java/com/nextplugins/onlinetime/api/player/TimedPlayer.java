package com.nextplugins.onlinetime.api.player;

import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.api.models.enums.RewardStatus;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class TimedPlayer {

    @Builder.Default private List<String> collectedRewards = new ArrayList<>();
    private String name;
    private long lastUpdateTime;

    @Builder.Default private long timeInServer = 0;

    public synchronized void addTime(long time) {
        this.timeInServer += time;
    }

    public synchronized void removeTime(long time) {
        this.timeInServer -= time;
    }

    public RewardStatus canCollect(Reward reward) {
        if (collectedRewards == null) collectedRewards = new ArrayList<>();
        if (collectedRewards.contains(reward.getName())) return RewardStatus.COLLECTED;
        if (timeInServer < reward.getTime()) return RewardStatus.NO_TIME;

        return RewardStatus.CAN_COLLECT;
    }

}
