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

        ItemBuilder itemBuilder;

        if (section.contains("id")) {

            itemBuilder = new ItemBuilder(
                    Material.getMaterial(section.getInt("id")),
                    1,
                    (short) section.getInt("data")
            );

        } else {

            itemBuilder = new ItemBuilder(section.getString("head"));

        }

        return itemBuilder.wrap();

    }

}
