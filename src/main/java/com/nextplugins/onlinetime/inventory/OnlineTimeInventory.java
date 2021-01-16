package com.nextplugins.onlinetime.inventory;

import com.google.inject.Inject;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.models.enums.RewardStatus;
import com.nextplugins.onlinetime.utils.ItemBuilder;
import com.nextplugins.onlinetime.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimeInventory extends PagedInventory {

    private final Map<String, Integer> playerRewardFilter = new HashMap<>();

    @Inject private RewardManager rewardManager;
    @Inject private TimedPlayerManager timedPlayerManager;

    public OnlineTimeInventory() {
        super(
                "online-time.main",
                "Seu tempo no servidor",
                6 * 9
        );

        NextOnlineTime.getInstance().getInjector().injectMembers(this);

    }

    @Override
    protected void configureViewer(PagedViewer viewer) {

        ViewerConfigurationImpl.Paged pagedViewer = viewer.getConfiguration();

        pagedViewer.itemPageLimit(21);
        pagedViewer.border(Border.of(1, 1, 2, 1));

    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        Player player = viewer.getPlayer();
        TimedPlayer timedPlayer = this.timedPlayerManager.getByName(player.getName());

        editor.setItem(48, InventoryItem.of(
                new ItemBuilder(viewer.getPlayer().getName())
                        .name("&a" + viewer.getPlayer().getName())
                        .setLore(
                                "&7Confira seu progresso abaixo:",
                                "&7Total de tempo online: &f" + TimeUtils.formatTime(timedPlayer.getTimeInServer())
                        )
                        .wrap()
                )
        );

        editor.setItem(49, changeFilterInventoryItem(viewer));

        editor.setItem(50, InventoryItem.of(
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("&6TOP Online")
                        .setLore("&7Clique para ver os top jogadores", "&7onlines no servidor")
                        .wrap()
                ).defaultCallback(callback -> {

                    TopOnlineTimeInventory topOnlineTimeInventory = new TopOnlineTimeInventory().init();
                    topOnlineTimeInventory.openInventory(player);

                })
        );

    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {

        List<InventoryItemSupplier> items = new ArrayList<>();

        Player player = viewer.getPlayer();
        TimedPlayer timedPlayer = timedPlayerManager.getByName(player.getName());

        int rewardFilter = playerRewardFilter.getOrDefault(viewer.getName(), -1);

        for (String name : rewardManager.getRewards().keySet()) {

            Reward reward = rewardManager.getByName(name);
            RewardStatus rewardStatus = timedPlayer.canCollect(reward);

            if (rewardFilter != -1 && rewardFilter != rewardStatus.getCode()) continue;

            List<String> replacedLore = new ArrayList<>();
            for (String line : MessageValue.get(MessageValue::rewardLore)) {

                if (line.contains("%reward_description%")) replacedLore.addAll(reward.getDescription());
                else {

                    replacedLore.add(line
                            .replace("%time%", TimeUtils.formatTime(reward.getTime()))
                            .replace("%collect_message%", rewardStatus.getMessage())
                    );

                }

            }

            items.add(() -> InventoryItem.of(
                    new ItemBuilder(reward.getIcon())
                            .name(reward.getColoredName())
                            .setLore(replacedLore)
                            .wrap()
                    ).defaultCallback(callback -> {

                        if (!rewardStatus.isCanCollect()) {

                            player.sendMessage(rewardStatus.getMessage());
                            return;

                        }

                        int avaliableSpaces = 0;
                        for (ItemStack content : player.getInventory().getContents()) {

                            if (content != null && content.getType() != Material.AIR) continue;
                            ++avaliableSpaces;

                        }

                        if (avaliableSpaces < reward.getCommands().size()) {

                            player.sendMessage(MessageValue.get(MessageValue::noSpace)
                                    .replace("%spaces%", String.valueOf(reward.getCommands().size() - avaliableSpaces))
                            );
                            return;

                        }

                        for (String command : reward.getCommands()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                        }

                        player.sendMessage(
                                MessageValue.get(MessageValue::collectedReward)
                                        .replace("%reward%", reward.getColoredName())
                        );

                        timedPlayer.getCollectedRewards().add(name);

                        if (NextOnlineTime.getInstance().getConfig().getBoolean("type")) {

                            timedPlayer.removeTime(reward.getTime());

                            player.sendMessage(MessageValue.get(MessageValue::usedTime)
                                    .replace("%time%", TimeUtils.formatTime(reward.getTime()))
                            );

                        }

                        callback.updateInventory();

                    })
            );


        }

        return items;

    }

    @Override
    protected void update(Viewer viewer, InventoryEditor editor) {

        super.update(viewer, editor);
        configureInventory(viewer, viewer.getEditor());

    }

    private InventoryItem changeFilterInventoryItem(Viewer viewer) {
        AtomicInteger currentFilter = new AtomicInteger(playerRewardFilter.getOrDefault(viewer.getName(), -1));
        return InventoryItem.of(new ItemBuilder(Material.HOPPER)
                .name("&6Filtro de recompensas")
                .setLore(
                        "&7Veja apenas as recompensas que deseja",
                        "",
                        getColorByFilter(currentFilter.get(), -1) + " Todas as recompensas",
                        getColorByFilter(currentFilter.get(), 0) + " Recompensas liberadas",
                        getColorByFilter(currentFilter.get(), 1) + " Recompensas bloqueadas",
                        getColorByFilter(currentFilter.get(), 2) + " Recompensas coletadas",
                        "",
                        "&aClique para mudar o filtro!"
                )
                .wrap())
                .defaultCallback(event -> {

                    playerRewardFilter.put(viewer.getName(), currentFilter.incrementAndGet() > 2 ? -1 : currentFilter.get());
                    event.updateInventory();

                });
    }

    private String getColorByFilter(int currentFilter, int loopFilter) {
        return currentFilter == loopFilter ? " &b➤" : "&8";
    }

}
