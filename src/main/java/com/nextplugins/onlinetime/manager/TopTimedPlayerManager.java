package com.nextplugins.onlinetime.manager;

import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import lombok.Getter;

import java.util.LinkedHashMap;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class TopTimedPlayerManager {

    @Getter private final LinkedHashMap<String, Long> topPlayers = new LinkedHashMap<>();

    public void addPlayer(TimedPlayer timedPlayer) {
        this.topPlayers.put(timedPlayer.getName(), timedPlayer.getTimeInServer());
    }

}
