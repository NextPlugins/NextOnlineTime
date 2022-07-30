package com.nextplugins.onlinetime.listener;

import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.utils.TimeUtils;
import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class CheckUseListener implements Listener {

    private final TimedPlayerManager timedPlayerManager;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
	   ItemStack item = event.getItem();
	   if (item == null || item.getType() == Material.AIR) return;

	   NBTItem nbtItem = new NBTItem(item);
	   if (!nbtItem.hasKey("timeInCheck")) return;

	   long timeInCheck = nbtItem.getLong("timeInCheck");
	   this.timedPlayerManager.getByName(event.getPlayer().getName()).addTime(timeInCheck);

	   event.getPlayer().sendMessage(
			 MessageValue.get(MessageValue::checkActivated)
				    .replace("%time%", TimeUtils.format(timeInCheck))
	   );

	   event.getPlayer().setItemInHand(null);
    }
}
