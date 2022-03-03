package com.nextplugins.onlinetime.npc.manager;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.npc.runnable.NPCRunnable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class NPCManager {

    protected final NextOnlineTime plugin = NextOnlineTime.getInstance();
    protected final PluginManager MANAGER = Bukkit.getPluginManager();

    protected final String PROTOCOL_LIB = "ProtocolLib";
    protected final String HOLOGRAPHIC_DISPLAYS = "HolographicDisplays";

    @Getter private boolean enabled;
    @Getter private Runnable runnable;

    public void init() {
        if (!MANAGER.isPluginEnabled(PROTOCOL_LIB) || !MANAGER.isPluginEnabled(HOLOGRAPHIC_DISPLAYS)) {
            plugin.getLogger().warning(
                String.format("Dependências não encontradas (%s, %s) O NPC não será usado.",
                    PROTOCOL_LIB,
                    HOLOGRAPHIC_DISPLAYS
                )
            );

            return;
        }

        runnable = new NPCRunnable(plugin);
        runnable.run();

        enabled = true;
    }

}
