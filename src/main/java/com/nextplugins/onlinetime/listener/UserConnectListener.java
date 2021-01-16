package com.nextplugins.onlinetime.listener;

import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.ColorUtils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class UserConnectListener implements Listener {

    private final TimedPlayerManager timedPlayerManager;
    private final ConversorManager conversorManager;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {

        if (!conversorManager.isConverting()) return;

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(ColorUtils.colored("&cO servidor está no meio de uma conversão, aguarde"));

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        this.timedPlayerManager.getByName(event.getPlayer().getName());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        this.timedPlayerManager.purge(event.getPlayer());

    }

}
