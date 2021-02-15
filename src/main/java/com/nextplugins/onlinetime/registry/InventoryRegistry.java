package com.nextplugins.onlinetime.registry;

import com.nextplugins.onlinetime.inventory.OnlineTimeInventory;
import com.nextplugins.onlinetime.inventory.TopOnlineTimeInventory;
import lombok.Getter;

import javax.inject.Singleton;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Singleton
public class InventoryRegistry {

    private final OnlineTimeInventory mainInventory = new OnlineTimeInventory();
    private final TopOnlineTimeInventory topInventory = new TopOnlineTimeInventory();

    public void init() {

        this.mainInventory.init();
        this.topInventory.init();

    }

}
