package com.nextplugins.onlinetime.registry;

import com.nextplugins.onlinetime.view.OnlineTimeView;
import com.nextplugins.onlinetime.view.TopOnlineTimeView;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
public class InventoryRegistry {

    private OnlineTimeView mainInventory;
    private TopOnlineTimeView topInventory;

    public void init() {

        mainInventory = new OnlineTimeView().init();
        topInventory = new TopOnlineTimeView().init();

    }

}
