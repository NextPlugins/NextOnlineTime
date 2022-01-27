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
            if (timedPlayer.getTimeInServer() >= 1740001 && timedPlayer.getTimeInServer() <= 2032490) {
                player.sendTitle("§9§lGG!", "§fVocê está online a mais de 30 minutos.");
                player.sendMessage("§a§lBOA! §aUma nova recompensa foi liberada no §f/tempo§a.");
                return;
            }
            if (timedPlayer.getTimeInServer() >= 35940000 && timedPlayer.getTimeInServer() <= 36232490) {
                player.sendTitle("§9§lGG!", "§fVocê está online a mais de 10 horas.");
                player.sendMessage("§a§lBOA! §aUma nova recompensa foi liberada no §f/tempo§a.");
                return;
            }
            if (timedPlayer.getTimeInServer() >= 14340000 && timedPlayer.getTimeInServer() <= 14632490) {
                player.sendTitle("§9§lGG!", "§fVocê está online a mais de 4 horas.");
                player.sendMessage("§a§lBOA! §aUma nova recompensa foi liberada no §f/tempo§a.");
                return;
            }
        }

    }

}
