package com.nextplugins.onlinetime.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

    public void purge(Player player) {

        TimedPlayer timedPlayer = this.players.getOrDefault(player.getName(), null);
        if (timedPlayer == null) return;

        this.purge(timedPlayer);
        this.players.remove(player.getName());


    }

    public void purge(TimedPlayer timedPlayer) {
        this.timedPlayerDAO.saveOne(timedPlayer);
    }

    public Collection<TimedPlayer> getPlayers() {
        return this.players.values();
    }
}
