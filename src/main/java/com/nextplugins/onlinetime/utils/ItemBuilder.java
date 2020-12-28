package com.nextplugins.onlinetime.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material type) {
        this(new ItemStack(type));
    }

    public ItemBuilder(Material type, int quantity, short data) {
        this(new ItemStack(type, quantity, data));
    }

    public ItemBuilder(String name) {
        item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (!name.contains("http://textures.minecraft.net/texture/")) meta.setOwner(name);
        else {

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            byte[] encodedData = Base64.getEncoder()
                    .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", name).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        item.setItemMeta(meta);
    }

    public ItemBuilder setTag(String path, String mine) {
        net.minecraft.server.v1_8_R3.ItemStack itemNBT = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = (itemNBT.hasTag() ? itemNBT.getTag() : new NBTTagCompound());
        tag.set(path, new NBTTagString(mine));
        item = CraftItemStack.asBukkitCopy(itemNBT);
        return this;
    }

    public ItemBuilder setTag(String path, double value) {
        net.minecraft.server.v1_8_R3.ItemStack itemNBT = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = (itemNBT.hasTag() ? itemNBT.getTag() : new NBTTagCompound());
        tag.set(path, new NBTTagDouble(value));
        item = CraftItemStack.asBukkitCopy(itemNBT);
        return this;
    }

    public ItemBuilder changeItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = item.getItemMeta();
        consumer.accept(itemMeta);
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder changeItem(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    public ItemBuilder name(String name) {
        return changeItemMeta(it -> it.setDisplayName(ColorUtils.colored(name)));
    }

    public ItemBuilder setLore(String... lore) {
        return changeItemMeta(it -> it.setLore(Arrays.asList(ColorUtils.colored(lore))));
    }

    public ItemBuilder setLore(List<String> lore) {
        return changeItemMeta(it -> it.setLore(lore));
    }

    public ItemBuilder addLore(List<String> lore) {
        if (lore == null || lore.isEmpty()) return this;

        return changeItemMeta(meta -> {
            List<String> list = meta.getLore();
            list.addAll(lore);
            meta.setLore(list);
        });
    }

    public ItemStack wrap() {
        return item;
    }

}