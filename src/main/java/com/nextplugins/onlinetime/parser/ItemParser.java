package com.nextplugins.onlinetime.parser;

import com.nextplugins.onlinetime.utils.ColorUtil;
import com.nextplugins.onlinetime.utils.ItemBuilder;
import com.nextplugins.onlinetime.utils.TypeUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

public final class ItemParser {

    public ItemStack parseSection(ConfigurationSection section) {

        try {

            ItemBuilder itemBuilder;

            if (section.contains("head")) itemBuilder = new ItemBuilder(section.getString("head"));

            else {
                itemBuilder = new ItemBuilder(TypeUtil.convertFromLegacy(
                    section.getString("material"),
                    section.contains("data") ? (short) section.getInt("data") : 0)
                );
            }

            if (section.contains("name")) itemBuilder.name(ColorUtil.colored(section.getString("name")));
            if (section.contains("glow") && section.getBoolean("glow")) itemBuilder.glow();

            if (section.contains("description")) {

                final List<String> lore = new ArrayList<>();
                for (String description : section.getStringList("description")) {
                    lore.add(ColorUtil.colored(description));
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
