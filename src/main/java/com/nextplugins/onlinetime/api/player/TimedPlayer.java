package com.nextplugins.onlinetime.api.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@NoArgsConstructor
public class TimedPlayer {

    private long timeInServer = 0;
    private final List<String> collectedRewards = new ArrayList<>();

}
