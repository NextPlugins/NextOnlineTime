package com.nextplugins.onlinetime.manager;

import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Singleton
public class TopTimedPlayerManager {

    private final LinkedHashMap<String, Long> topPlayers = new LinkedHashMap<>();
    private long nextUpdate;

    public void addPlayer(TimedPlayer timedPlayer) {
        this.topPlayers.put(timedPlayer.getName(), timedPlayer.getTimeInServer());
    }

}
