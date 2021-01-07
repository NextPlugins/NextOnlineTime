package com.nextplugins.onlinetime.dao.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.onlinetime.api.player.TimedPlayer;

public final class TimedPlayerAdapter implements SQLResultAdapter<TimedPlayer> {

    @Override
    public TimedPlayer adaptResult(SimpleResultSet resultSet) {

        TimedPlayer timedPlayer = TimedPlayer.builder()
                .timeInServer(resultSet.get("time"))
                .name(resultSet.get("name"))
                .build();

        String collectedRewards = resultSet.get("collectedRewards");
        if (collectedRewards.equalsIgnoreCase("")) return timedPlayer;

        for (String name : collectedRewards.split(",")) timedPlayer.getCollectedRewards().add(name);

        return timedPlayer;

    }

}
