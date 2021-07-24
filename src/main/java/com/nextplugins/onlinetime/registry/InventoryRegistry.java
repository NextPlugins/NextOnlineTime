package com.nextplugins.onlinetime.registry;

import com.nextplugins.onlinetime.view.OnlineTimeView;
import com.nextplugins.onlinetime.view.TopOnlineTimeView;
import lombok.Getter;

import javax.inject.Singleton;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Singleton
public class InventoryRegistry {

    private OnlineTimeView mainInventory;
    private TopOnlineTimeView topInventory;

    public void init() {

        this.mainInventory = new OnlineTimeView().init();
        this.topInventory = new TopOnlineTimeView().init();

    }

}
