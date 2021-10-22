package com.nextplugins.onlinetime.api.reward;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class Reward {

    private final String name;
    private final String coloredName;
    private final String permission;

    private final ItemStack icon;

    private final long time;

    private final List<String> description;
    private final List<String> commands;

}
