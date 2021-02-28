package com.nextplugins.onlinetime.listener;

import com.nextplugins.onlinetime.manager.NPCManager;
import lombok.AllArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class InteractNPCListener implements Listener {

    private final NPCManager npcManager;

    @EventHandler
    public void interactNpc(NPCRightClickEvent event) {

        if (event.getNPC().getId() != this.npcManager.getNpc().getId()) return;
        event.getClicker().performCommand("tempo menu");

    }

}
