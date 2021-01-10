package com.nextplugins.onlinetime.inventory;

import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.nextplugins.onlinetime.NextOnlineTime;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TopOnlineTimeInventory extends SimpleInventory {

    

    public TopOnlineTimeInventory(String id, String title, int size) {

        super(
                "online-time.top",
                "TOP Online",
                4 * 9
        );

        NextOnlineTime.getInstance().getInjector().injectMembers(this);

    }

}
