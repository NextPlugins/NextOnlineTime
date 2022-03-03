package com.nextplugins.onlinetime.listener.registry;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.listener.CheckUseListener;
import com.nextplugins.onlinetime.listener.UpdateCheckerListener;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.listener.chat.LegendChatListener;
import com.nextplugins.onlinetime.listener.chat.NChatListener;
import lombok.val;

public final class ListenerRegistry {

    public ListenerRegistry(NextOnlineTime plugin) {
        val pluginManager = plugin.getServer().getPluginManager();
        val logger = plugin.getLogger();
        val timedPlayerManager = plugin.getTimedPlayerManager();

        val updateCheckerListener = new UpdateCheckerListener();
        val checkUseListener = new CheckUseListener(timedPlayerManager);
        val userConnectListener = new UserConnectListener(
            timedPlayerManager,
            plugin.getConversorManager()
        );

        pluginManager.registerEvents(checkUseListener, plugin);
        pluginManager.registerEvents(userConnectListener, plugin);
        pluginManager.registerEvents(updateCheckerListener, plugin);

        if (pluginManager.isPluginEnabled("nChat")) {
            pluginManager.registerEvents(
                new NChatListener(
                    timedPlayerManager
                ),
                plugin
            );

            logger.info("[Chat] Dependência 'nChat' sendo utilizada como plugin de Chat");
        } else if (pluginManager.isPluginEnabled("Legendchat")) {
            pluginManager.registerEvents(
                new LegendChatListener(timedPlayerManager),
                plugin
            );

            logger.info("[Chat] Dependência 'LegendChat' sendo utilizada como plugin de Chat");
        }
    }

}
