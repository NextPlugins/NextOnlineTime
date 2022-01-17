package com.nextplugins.onlinetime.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class PrefixUtils {

    static LuckPerms api = LuckPermsProvider.get();

    public static String getPrefix(Player p) {
        User user = api.getPlayerAdapter(Player.class).getUser(p);
        if (user != null) {
            return user.getCachedData().getMetaData().getPrefix().replace("&","§") + " ";
        }
        return "§7";
    }

    public static String getPrefix(String p) {
        User user = (User) api.getUserManager().getUser(p);
        if (user != null) {
            return user.getCachedData().getMetaData().getPrefix().replace("&","§") + " ";
        }
        return "§7";
    }

}
