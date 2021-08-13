package com.nextplugins.onlinetime.api.conversion.impl.atlas;

import com.google.gson.Gson;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.models.objects.AtlasTimedPlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class AtlasOnlineTimeAdapter implements SQLResultAdapter<TimedPlayer> {

    private static final Gson GSON = new Gson();

    @Override
    public TimedPlayer adaptResult(SimpleResultSet resultSet) {

        AtlasTimedPlayer atlasTimedPlayer = GSON.fromJson(resultSet.get("json").toString(), AtlasTimedPlayer.class);

        return TimedPlayer.builder()
            .name(atlasTimedPlayer.getUser())
            .timeInServer(atlasTimedPlayer.getTotalOnTime())
            .lastUpdateTime(atlasTimedPlayer.getLoggedInTime())
            .collectedRewards(atlasTimedPlayer.getPrizesCollecteds())
            .build();

    }

}
