package com.nextplugins.onlinetime.manager;

import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import lombok.Data;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class TopTimedPlayerManager {

    private final TimedPlayerDAO timedPlayerDAO;

    private final LinkedHashMap<String, Long> topPlayers = new LinkedHashMap<>();
    private long nextUpdate;

    public void addPlayer(TimedPlayer timedPlayer) {
        this.topPlayers.put(timedPlayer.getName(), timedPlayer.getTimeInServer());
    }

    public boolean checkUpdate() {
        if (nextUpdate > System.currentTimeMillis()) return false;

        nextUpdate = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        topPlayers.clear();

        val timedPlayers = timedPlayerDAO.selectAll("ORDER BY time DESC LIMIT 10");
        timedPlayers.forEach(this::addPlayer);

        return true;
    }

}
