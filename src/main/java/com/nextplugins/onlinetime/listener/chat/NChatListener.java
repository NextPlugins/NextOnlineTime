package com.nextplugins.onlinetime.listener.chat;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class NChatListener implements Listener {

    private final TimedPlayerManager timedPlayerManager;

    @EventHandler
    public void onPlayerChat(ChatMessageEvent event) {
        if (event.isCancelled()) return;

        val player = event.getSender();

        val timeInServer = this.timedPlayerManager.getByName(player.getName()).getTimeInServer();

        val timeFormatted = TimeUtils.formatOneLetter(timeInServer);

        event.setTagValue(
            "online_time",
            timeFormatted
        );
    }

}
