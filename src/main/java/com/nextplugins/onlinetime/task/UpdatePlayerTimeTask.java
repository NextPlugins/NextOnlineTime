package com.nextplugins.onlinetime.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Singleton
public class UpdatePlayerTimeTask implements Runnable {

    @Inject private TimedPlayerManager timedPlayerManager;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            TimedPlayer timedPlayer = this.timedPlayerManager.getByName(player.getName());

            timedPlayer.addTime(System.currentTimeMillis() - timedPlayer.getLastUpdateTime());
            timedPlayer.setLastUpdateTime(System.currentTimeMillis());

        }

    }

}
