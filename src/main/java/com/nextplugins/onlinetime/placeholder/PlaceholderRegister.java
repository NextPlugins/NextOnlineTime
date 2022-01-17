package com.nextplugins.onlinetime.placeholder;

import com.nextplugins.onlinetime.NextOnlineTime;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.utils.ColorUtil;
import com.nextplugins.onlinetime.utils.TimeUtils;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor(staticName = "of")
public final class PlaceholderRegister extends PlaceholderExpansion {

    private final NextOnlineTime plugin;

    @Override
    public String getName() {
        return this.plugin.getName();
    }

    @Override
    public String getIdentifier() {
        return "onlinetime";
    }

    @Override
    public List<String> getPlaceholders() {
        return Collections.singletonList("time");
    }

    @Override
    public String getAuthor() {
        return StringUtils.join(this.plugin.getDescription().getAuthors(), ",");
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {

        if (!params.equalsIgnoreCase("time")) return ColorUtil.colored("&cParametro inválido");

        TimedPlayer timedPlayer = this.plugin.getTimedPlayerManager().getByName(player.getName());
        return TimeUtils.formatOne(timedPlayer.getTimeInServer());

    }
}
