package com.nextplugins.onlinetime.api.player;

import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.models.enums.RewardStatus;
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

    private String name;
    private long lastUpdateTime;
    @Builder.Default private long timeInServer = 0;
    @Builder.Default private final List<String> collectedRewards = new ArrayList<>();

    public void addTime(long time) {
        this.timeInServer += time;
    }

    public RewardStatus canCollect(Reward reward) {

        if (this.collectedRewards.contains(reward.getName())) return RewardStatus.COLLECTED;
        if (this.timeInServer < reward.getTime()) return RewardStatus.NO_TIME;
        return RewardStatus.CAN_COLLECT;

    }

}
