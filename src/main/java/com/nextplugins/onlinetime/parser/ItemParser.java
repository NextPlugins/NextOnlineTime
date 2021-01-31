package com.nextplugins.onlinetime.parser;

import com.google.inject.Singleton;
import com.nextplugins.onlinetime.utils.ColorUtils;
import com.nextplugins.onlinetime.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Singleton
public class ItemParser {

    public ItemStack parseSection(ConfigurationSection section) {

        try {

            ItemBuilder itemBuilder;

            if (section.contains("head")) itemBuilder = new ItemBuilder(section.getString("head"));

            else {

                itemBuilder = new ItemBuilder(
                        section.contains("material")
                                ? Material.valueOf(section.getString("material"))
                                : Material.getMaterial(section.getInt("id")),
                        1,
                        section.contains("data") ? (short) section.getInt("data") : 0
                );
            }

            if (section.contains("name")) itemBuilder.name(ColorUtils.colored(section.getString("name")));

            if (section.contains("description")) {

                final List<String> lore = new ArrayList<>();
                for (String description : section.getStringList("description")) {
                    lore.add(ColorUtils.colored(description));
                }

                itemBuilder.setLore(lore);
            }

            return itemBuilder.wrap();

        } catch (Throwable throwable) {

            throwable.printStackTrace();
            return null;

        }

    }

}
