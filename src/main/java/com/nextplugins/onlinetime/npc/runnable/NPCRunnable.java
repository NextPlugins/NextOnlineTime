package com.nextplugins.onlinetime.npc.runnable;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nextplugins.onlinetime.configuration.values.NPCValue;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class NPCRunnable implements Runnable, Listener {

    private final Plugin plugin;
    private final NPCPool npcPool;
    @Getter private int npcId = -1;

    public NPCRunnable(Plugin plugin) {
        this.plugin = plugin;
        this.npcPool = NPCPool.builder(plugin)
              .spawnDistance(60)
              .actionDistance(30)
              .tabListRemoveTicks(20)
              .build();

        this.plugin.getServer().getPluginManager().registerEvents(
              this,
              this.plugin
        );
    }

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
        Bukkit.getScheduler().runTask(this.plugin, () -> spawn(location, NPCValue.get(NPCValue::npcName), NPCValue.get(NPCValue::skinNick), NPCValue.get(NPCValue::hologramMessage), NPCValue.get(NPCValue::heightToAdd)));
    }

    /**
     * Spawn npc and hologram
     *
     * @param location to spawn the npc and hologram
     */
    public void spawn(Location location, String npcName, String skinNick, List<String> hologramMessage, double hologramAddition) {
        clear();

        val profile = new Profile(skinNick);
        profile.complete();
        profile.setName(npcName);
        profile.setUniqueId(UUID.randomUUID());

        val yaw = location.getYaw();
        val pitch = location.getPitch();

        val npc = NPC.builder()
              .profile(profile)
              .location(location)
              .imitatePlayer(false)
              .lookAtPlayer(NPCValue.get(NPCValue::lookCLose))
              .spawnCustomizer((spawnedNpc, player) -> spawnedNpc.rotation().queueRotate(yaw, pitch).send(player))
              .build(this.npcPool);

        npc.visibility().queueSpawn();

        npcId = npc.getEntityId();

        // hologram implementation
        if (hologramMessage.isEmpty()) return;

        val hologram = HologramsAPI.createHologram(plugin, location.clone().add(0, hologramAddition, 0));
        for (int i = 0; i < hologramMessage.size(); i++) {
            String line = hologramMessage.get(i);
            hologram.insertTextLine(i, line);
        }
    }

    public void clear() {
        this.npcPool.getNpc(npcId).ifPresent(npc -> npc.visibility().queueDestroy());

        npcId = -1;
        HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
    }

    @EventHandler
    public void handleInteract(PlayerNPCInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getUseAction() == PlayerNPCInteractEvent.EntityUseAction.INTERACT) {
            if (event.getNPC().getEntityId() == npcId) {
                player.performCommand("tempo menu");
            }
        }
    }

    @EventHandler
    public void handleShow(PlayerNPCShowEvent event) {
        final NPC npc = event.getNPC();

        event.send(npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true));
    }

}
