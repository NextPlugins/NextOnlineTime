package com.nextplugins.onlinetime.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class TimedPlayerManager {

    @Getter @Inject private TimedPlayerDAO timedPlayerDAO;

    private final Map<String, TimedPlayer> players = new HashMap<>();

    public TimedPlayer getByName(String name) {

        TimedPlayer timedPlayer = this.players.getOrDefault(name, null);
        if (timedPlayer == null) {

            timedPlayer = this.timedPlayerDAO.selectOne(name);

            if (timedPlayer == null) {

                timedPlayer = TimedPlayer.builder()
                        .name(name)
                        .build();

                this.timedPlayerDAO.insertOne(timedPlayer);

            }

            this.players.put(name, timedPlayer);

        }

        return timedPlayer;

    }

}
