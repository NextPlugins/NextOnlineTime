package com.nextplugins.onlinetime.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.configuration.values.NPCValue;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;

import javax.inject.Singleton;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class NPCManager {

    protected final NextOnlineTime plugin = NextOnlineTime.getInstance();
    protected final PluginManager MANAGER = Bukkit.getPluginManager();
    protected final String CITIZENS = "Citizens";
    protected final String HOLOGRAPHIC_DISPLAYS = "HolographicDisplays";

    public static boolean isEnabled;

    @Getter private NPC npc;
    @Getter private Hologram hologram;

    public void init() {

        if (!MANAGER.isPluginEnabled(CITIZENS) || !MANAGER.isPluginEnabled(HOLOGRAPHIC_DISPLAYS)) {

            plugin.getLogger().warning(
                    String.format("Dependências não encontradas (%s, %s) O NPC não será usado.",
                            CITIZENS,
                            HOLOGRAPHIC_DISPLAYS
                    )
            );

            isEnabled = false;
            return;

        }

        Location location = NPCValue.get(NPCValue::position);
        if (location == null) return;

        spawnDefault(location);

    }


    /**
     * Default spawn of npc & hologram
     */
    public void spawnDefault(Location location) {

        Bukkit.getScheduler().runTask(this.plugin, () -> spawn(
                location,
                NPCValue.get(NPCValue::npcName),
                NPCValue.get(NPCValue::skinNick),
                NPCValue.get(NPCValue::hologramMessage),
                NPCValue.get(NPCValue::heightToAdd)
        ));

    }

    /**
     * Spawn npc and hologram
     *
     * @param location to spawn the npc and hologram
     */
    public boolean spawn(Location location,
                         String npcName,
                         String skinNick,
                         List<String> hologramMessage,
                         double hologramAddition) {

        // prevent duplicate npc / holograms
        despawn();

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        // npc implementation
        NPC npc = registry.createNPC(EntityType.PLAYER, npcName);
        npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, Bukkit.getOfflinePlayer(skinNick).getName());
        npc.setProtected(true);
        npc.spawn(location);

        if (NPCValue.get(NPCValue::lookCLose)) {

            LookClose lookClose = new LookClose();
            lookClose.lookClose(true);

            npc.addTrait(lookClose);

        }

        this.npc = npc;

        // hologram implementation
        if (hologramMessage.isEmpty()) return true;

        Hologram hologram = HologramsAPI.createHologram(this.plugin, location.clone().add(0, hologramAddition, 0));

        for (int i = 0; i < hologramMessage.size(); i++) {

            String line = hologramMessage.get(i);
            hologram.insertTextLine(i, line);

        }

        this.hologram = hologram;

        return true;

    }

    /**
     * Delete cached npc and hologram
     */
    public void despawn() {

        if (npc == null || hologram == null) return;

        npc.destroy();
        hologram.delete();

    }

}
