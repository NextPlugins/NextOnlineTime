package com.nextplugins.onlinetime.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.adapter.TimedPlayerAdapter;

@Singleton
public final class TimedPlayerDAO {

    private final String table = "onlinetime_players";
    @Inject private SQLProvider sqlProvider;

    public void createTable() {
        sqlProvider.executor().updateQuery("CREATE TABLE IF NOT EXISTS " + table + "(" +
                "name VARCHAR(16) NOT NULL PRIMARY KEY," +
                "time INTEGER(16) NOT NULL," +
                "collectedRewards TEXT NOT NULL" +
                ");");
    }

    public TimedPlayer selectOne(String name) {

        return sqlProvider.executor().resultOneQuery(
                "SELECT * FROM " + table + " WHERE `name` = '" + name + "'",
                TimedPlayerAdapter.class
        );
    }

    public void insertOne(TimedPlayer timedPlayer) {
        sqlProvider.executor().updateOneQuery(
                "INSERT INTO " + table + " VALUES(?,?,?);",
                TimedPlayerAdapter.class,
                timedPlayer
        );
    }

}
