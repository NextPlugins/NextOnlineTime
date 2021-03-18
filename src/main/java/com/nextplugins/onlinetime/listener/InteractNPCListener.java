package com.nextplugins.onlinetime.listener;

import com.nextplugins.onlinetime.npc.manager.NPCManager;
import com.nextplugins.onlinetime.npc.runnable.NPCRunnable;
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

        if (event.getNPC() == null || !this.npcManager.isEnabled()) return;

        NPCRunnable runnable = (NPCRunnable) this.npcManager.getRunnable();
        if (runnable.getNPC() == null || event.getNPC().getId() != runnable.getNPC().getId()) return;

        event.getClicker().performCommand("tempo menu");

    }

}
