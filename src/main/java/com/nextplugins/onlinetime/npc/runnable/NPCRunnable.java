package com.nextplugins.onlinetime.npc.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nextplugins.onlinetime.configuration.values.NPCValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public class NPCRunnable implements Runnable {

    private final Plugin plugin;
    @Getter
    private int npcId;

    @Override
    public void run() {
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
        clear();

        val registry = CitizensAPI.getNPCRegistry();

        // npc implementation
        val npc = registry.createNPC(EntityType.PLAYER, npcName);
        npc.data().set("player-skin-name", skinNick);
        npc.data().set("nextonlinetime", true);
        npc.setProtected(true);
        npc.spawn(location);

        if (NPCValue.get(NPCValue::lookCLose)) {
            val lookClose = new LookClose();
            lookClose.lookClose(true);

            npc.addTrait(lookClose);
        }

        npcId = npc.getId();

        // hologram implementation
        if (hologramMessage.isEmpty()) return true;

        val hologram = HologramsAPI.createHologram(plugin, location.clone().add(0, hologramAddition, 0));
        for (int i = 0; i < hologramMessage.size(); i++) {
            String line = hologramMessage.get(i);
            hologram.insertTextLine(i, line);
        }

        return true;
    }

    public void clear() {
        try {
            for (val npc : CitizensAPI.getNPCRegistry()) {
                if (!npc.data().has("nextonlinetime")) continue;

                npc.despawn();
                npc.destroy();
            }

        } catch (Exception exception) {
            if (npcId != 0) {
                val npc = CitizensAPI.getNPCRegistry().getById(npcId);
                if (npc != null) {
                    npc.despawn();
                    npc.destroy();
                }
            }
        }

        npcId = 0;
        HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
    }

}
