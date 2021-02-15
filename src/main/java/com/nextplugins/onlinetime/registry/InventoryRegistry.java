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

    private OnlineTimeInventory mainInventory;
    private TopOnlineTimeInventory topInventory;

    public void init() {

        this.mainInventory = new OnlineTimeInventory().init();
        this.topInventory = new TopOnlineTimeInventory().init();

    }

}
