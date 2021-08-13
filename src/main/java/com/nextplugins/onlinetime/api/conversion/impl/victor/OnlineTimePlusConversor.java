package com.nextplugins.onlinetime.api.conversion.impl.victor;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.api.conversion.Conversor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;

import java.util.Set;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class OnlineTimePlusConversor extends Conversor {

    private final String table;

    public OnlineTimePlusConversor(String conversorName, String table, SQLConnector connector) {
        super(
            conversorName,
            new SQLExecutor(connector)
        );

        this.table = table;
    }

    @Override
    public Set<TimedPlayer> lookupPlayers() {

        return getExecutor().resultManyQuery(
            "SELECT * FROM " + table,
            statement -> {
            },
            OnlineTimePlusAdapter.class);

    }

}
