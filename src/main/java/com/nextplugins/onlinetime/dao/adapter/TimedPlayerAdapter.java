package com.nextplugins.onlinetime.dao.adapter;

import com.henryfabio.sqlprovider.common.adapter.SQLAdapter;
import com.henryfabio.sqlprovider.common.result.SimpleResultSet;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TimedPlayerAdapter implements SQLAdapter<TimedPlayer> {

    @Override
    public TimedPlayer adaptResult(SimpleResultSet resultSet) {

        TimedPlayer timedPlayer = new TimedPlayer();
        timedPlayer.setTimeInServer(resultSet.get("time"));

        String collectedRewards = resultSet.get("collectedRewards");
        if (collectedRewards.equalsIgnoreCase("")) return timedPlayer;

        for (String name : collectedRewards.split(",")) timedPlayer.getCollectedRewards().add(name);

        return timedPlayer;

    }

    @Override
    public void adaptStatement(PreparedStatement statement, TimedPlayer timedPlayer) throws SQLException {

        statement.setString(1, timedPlayer.getName());
        statement.setLong(2, timedPlayer.getTimeInServer());
        statement.setString(3, StringUtils.join(timedPlayer.getCollectedRewards(), ","));

    }

}
