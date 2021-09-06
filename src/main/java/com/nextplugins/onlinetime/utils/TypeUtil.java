package com.nextplugins.onlinetime.utils;

import com.nextplugins.onlinetime.NextOnlineTime;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TypeUtil {

    public static ItemStack convertFromLegacy(String materialName, int damage) {
        try {
            return new ItemStack(Material.valueOf(materialName), 1, (short) damage);
        } catch (Exception exception) {
            try {
                val material = Material.valueOf("LEGACY_" + materialName);
                return new ItemStack(Bukkit.getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, (byte) damage)));
            } catch (Exception error) {
                NextOnlineTime.getInstance().getLogger().warning("O material " + materialName + " é nulo, verifique a categories.yml");
                return null;
            }
        }
    }

}
