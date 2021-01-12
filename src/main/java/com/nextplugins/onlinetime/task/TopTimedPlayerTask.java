package com.nextplugins.onlinetime.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import com.nextplugins.onlinetime.manager.TopTimedPlayerManager;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class TopTimedPlayerTask implements Runnable {

    @Inject private TimedPlayerDAO timedPlayerDAO;
    @Inject private TopTimedPlayerManager topTimedPlayerManager;

    @Override
    public void run() {

        Set<TimedPlayer> timedPlayers = this.timedPlayerDAO.selectAll("ORDER BY time DESC LIMIT 10");

        this.topTimedPlayerManager.getTopPlayers().clear();
        timedPlayers.forEach(this.topTimedPlayerManager::addPlayer);

        this.topTimedPlayerManager.setNextUpdate(Instant.now()
                .plusMillis(TimeUnit.MINUTES.toMillis(30))
                .toEpochMilli());

    }
}
