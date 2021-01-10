package com.nextplugins.onlinetime.parser;

import com.google.inject.Singleton;
import com.nextplugins.onlinetime.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class ItemParser {

    public ItemStack parseSection(ConfigurationSection section) {

        return new ItemBuilder(
                Material.getMaterial(section.getInt("id")),
                1,
                (short) section.getInt("data")
        ).wrap();

    }

}
