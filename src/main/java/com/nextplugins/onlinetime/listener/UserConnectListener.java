package com.nextplugins.onlinetime.listener;

import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.ColorUtils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class UserConnectListener implements Listener {

    private final TimedPlayerManager timedPlayerManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.timedPlayerManager.getByName(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.timedPlayerManager.purge(event.getPlayer());
    }

}
