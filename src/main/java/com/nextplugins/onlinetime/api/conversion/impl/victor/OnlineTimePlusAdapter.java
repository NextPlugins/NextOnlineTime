package com.nextplugins.onlinetime.api.conversion.impl.victor;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.onlinetime.api.player.TimedPlayer;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimePlusAdapter implements SQLResultAdapter<TimedPlayer> {

    @Override
    public TimedPlayer adaptResult(SimpleResultSet resultSet) {

        String player = resultSet.get("Jogador").toString();
        int secconds = Integer.parseInt(resultSet.get("Segundos").toString());

        return TimedPlayer.builder()
            .name(player)
            .timeInServer(TimeUnit.SECONDS.toMillis(secconds))
            .build();

    }

}
