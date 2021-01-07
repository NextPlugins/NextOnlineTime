package com.nextplugins.onlinetime.task;

import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class UpdatePlayerTimeTask implements Runnable {

    private final int time;
    private final TimeUnit timeUnit;
    private final TimedPlayerManager timedPlayerManager;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            TimedPlayer timedPlayer = this.timedPlayerManager.getByName(player.getName());
            timedPlayer.addTime(time, timeUnit);

        }

    }

}
