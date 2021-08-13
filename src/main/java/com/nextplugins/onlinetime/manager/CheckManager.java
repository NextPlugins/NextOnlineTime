package com.nextplugins.onlinetime.manager;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.configuration.values.FeatureValue;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.utils.EventAwaiter;
import com.nextplugins.onlinetime.utils.TimeUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public class CheckManager {

    private final TimedPlayerManager timedPlayerManager = NextOnlineTime.getInstance().getTimedPlayerManager();

    @Setter private ItemStack checkItem;
    private Consumer<AsyncPlayerChatEvent> consumer;

    public void sendCheckRequisition(Player player) {

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextOnlineTime.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> player.sendMessage(MessageValue.get(MessageValue::checkNoTime)))
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(true);

    }

    public void init() {

        consumer = event -> {

            Player player = event.getPlayer();

            long timeInMillis = TimeUtils.unformat(event.getMessage());
            if (timeInMillis < 1) {

                player.sendMessage(MessageValue.get(MessageValue::invalidTime));
                return;

            }

            TimedPlayer timedPlayer = timedPlayerManager.getByName(player.getName());
            if (timedPlayer.getTimeInServer() < timeInMillis) {

                player.sendMessage(MessageValue.get(MessageValue::noTime));
                return;

            }

            if (player.getInventory().firstEmpty() == -1) {

                player.sendMessage(MessageValue.get(MessageValue::noSpace).replace("%spaces%", "1"));
                return;

            }

            timedPlayer.removeTime(timeInMillis);
            timeInMillis *= 1 - (FeatureValue.get(FeatureValue::check) / 100);

            String time = TimeUtils.format(timeInMillis);

            ItemStack check = checkItem.clone();
            ItemMeta itemMeta = check.getItemMeta();

            itemMeta.setLore(itemMeta.getLore()
                    .stream()
                    .map(line -> line.replace("%time%", time))
                    .collect(Collectors.toList())
            );

            check.setItemMeta(itemMeta);

            NBTItem nbtItem = new NBTItem(check);
            nbtItem.setLong("timeInCheck", timeInMillis);

            player.getInventory().addItem(nbtItem.getItem());
            player.sendMessage(MessageValue.get(MessageValue::checkSucess));

        };

    }

}
