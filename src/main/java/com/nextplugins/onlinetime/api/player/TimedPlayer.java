package com.nextplugins.onlinetime.api.player;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class TimedPlayer {

    private String name;
    @Builder.Default private long timeInServer = 0;
    @Builder.Default private final List<String> collectedRewards = new ArrayList<>();

    public void addTime(int time, TimeUnit timeUnit) {
        this.timeInServer += timeUnit.toMillis(time);
    }

}
