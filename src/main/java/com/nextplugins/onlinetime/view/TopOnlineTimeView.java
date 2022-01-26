package com.nextplugins.onlinetime.view;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.ViewerConfiguration;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.manager.TopTimedPlayerManager;
import com.nextplugins.onlinetime.utils.ItemBuilder;
import com.nextplugins.onlinetime.utils.PrefixUtils;
import com.nextplugins.onlinetime.utils.TimeUtils;
import com.nextplugins.onlinetime.utils.TypeUtil;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class TopOnlineTimeView extends SimpleInventory {

    private final TopTimedPlayerManager topTimedPlayerManager;

    public TopOnlineTimeView() {

        super(
            "online-time.top",
            "TOP Online",
            5 * 9
        );

        topTimedPlayerManager = NextOnlineTime.getInstance().getTopTimedPlayerManager();
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        editor.setItem(40, DefaultItem.BACK.toInventoryItem(viewer));

        editor.setItem(32, InventoryItem.of(new ItemBuilder(TypeUtil.convertFromLegacy("DOUBLE_PLANT", 0))
            .name("&9§lPRÓXIMA ATUALIZAÇÃO")
            .setLore(
                    "",
                "&7 O top tempo será atualizado em",
                "&f " + TimeUtils.format(topTimedPlayerManager.getNextUpdate() - System.currentTimeMillis()),
                        ""
            )
            .wrap()));

        int slot = 10;
        int position = 1;

        for (String name : this.topTimedPlayerManager.getTopPlayers().keySet()) {

            if (slot == 17) slot = 21;
            if (slot > 23) break;

            long time = this.topTimedPlayerManager.getTopPlayers().get(name);

            editor.setItem(slot, InventoryItem.of(new ItemBuilder(name)
                .name("&a" + name + " &7#" + position)
                .setLore("", "&f Total de tempo: ", "&7 " + TimeUtils.format(time), "")
                .wrap()));

            ++slot;
            ++position;

        }

    }

    @Override
    protected void configureViewer(SimpleViewer viewer) {

        ViewerConfiguration configuration = viewer.getConfiguration();
        configuration.backInventory("online-time.main");

    }

}
