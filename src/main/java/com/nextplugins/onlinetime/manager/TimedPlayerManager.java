package com.nextplugins.onlinetime.manager;

import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class TimedPlayerManager {

    private final TimedPlayerDAO timedPlayerDAO;

    @Getter private final Map<String, TimedPlayer> players = new HashMap<>();

    public TimedPlayer getByName(String name) {
        var timedPlayer = players.getOrDefault(name, null);
        if (timedPlayer == null) {
            timedPlayer = timedPlayerDAO.selectOne(name);
            if (timedPlayer == null) {
                timedPlayer = TimedPlayer.builder().name(name).build();
                timedPlayerDAO.saveOne(timedPlayer);
            }

            timedPlayer.setLastUpdateTime(System.currentTimeMillis());
            players.put(name, timedPlayer);
        }

        return timedPlayer;
    }

    public void purge(Player player) {
        val timedPlayer = players.getOrDefault(player.getName(), null);
        if (timedPlayer == null) return;

        purge(timedPlayer);
        players.remove(player.getName());
    }

    public void purge(TimedPlayer timedPlayer) {
        timedPlayer.addTime(System.currentTimeMillis() - timedPlayer.getLastUpdateTime());
        timedPlayerDAO.saveOne(timedPlayer);
    }

}
