package com.nextplugins.onlinetime.manager;

import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import lombok.Data;
import lombok.val;

import java.time.Instant;
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
        if (nextUpdate <= System.currentTimeMillis()) return false;

        val timedPlayers = timedPlayerDAO.selectAll("ORDER BY time DESC LIMIT 10");

        topPlayers.clear();
        timedPlayers.forEach(this::addPlayer);

        nextUpdate = Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(30)).toEpochMilli();
        return true;
    }

}
