package com.nextplugins.onlinetime.task;

import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.TimeUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class UpdatePlayerTimeTask implements Runnable {

    private final TimedPlayerManager timedPlayerManager;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            TimedPlayer timedPlayer = this.timedPlayerManager.getByName(player.getName());

            timedPlayer.addTime(System.currentTimeMillis() - timedPlayer.getLastUpdateTime());
            timedPlayer.setLastUpdateTime(System.currentTimeMillis());
        }

    }

}
