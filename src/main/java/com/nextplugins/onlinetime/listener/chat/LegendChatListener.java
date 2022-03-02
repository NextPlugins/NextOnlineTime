package com.nextplugins.onlinetime.listener.chat;

import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.TimeUtils;
import com.nickuc.chat.api.events.PublicMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class LegendChatListener implements Listener {

    private final TimedPlayerManager timedPlayerManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PublicMessageEvent event) {
        if (event.isCancelled()) return;

        val player = event.getSender();

        val timeInServer = this.timedPlayerManager.getByName(player.getName()).getTimeInServer();

        val timeFormatted = TimeUtils.formatOneLetter(timeInServer);

        val textComponent = new TextComponent(timeFormatted);
        val hoverEvent = new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            TextComponent.fromLegacyText(ChatColor.GRAY + TimeUtils.format(timeInServer))
        );

        textComponent.setHoverEvent(hoverEvent);

        event.setTag(
            "online_time",
            textComponent
        );
    }

}
