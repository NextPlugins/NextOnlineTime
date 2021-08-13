package com.nextplugins.onlinetime.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationUtils {

    public static Location deserialize(String data) {

        if (!data.contains(",")) return null;
        String[] split = data.split(",");

        return new Location(
            Bukkit.getWorld(split[0]),
            Double.parseDouble(split[1]),
            Double.parseDouble(split[2]),
            Double.parseDouble(split[3]),
            Float.parseFloat(split[4]),
            Float.parseFloat(split[5])
        );
    }

    public static String serialize(Location data) {
        return data.getWorld().getName() + ","
            + data.getX() + ","
            + data.getY() + ","
            + data.getZ() + ","
            + data.getYaw() + ","
            + data.getPitch();
    }

}
