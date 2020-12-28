package com.nextplugins.onlinetime.api.player;

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
    @Builder.Default private long timeInServer = 0;
    @Builder.Default private final List<String> collectedRewards = new ArrayList<>();

}
