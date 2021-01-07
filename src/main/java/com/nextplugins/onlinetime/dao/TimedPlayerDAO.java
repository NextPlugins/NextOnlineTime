package com.nextplugins.onlinetime.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.adapter.TimedPlayerAdapter;
import org.apache.commons.lang3.StringUtils;

@Singleton
public final class TimedPlayerDAO {

    private final String table = "onlinetime_players";
    @Inject private SQLExecutor sqlExecutor;

    public void createTable() {

        this.sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + table + "(" +
                "name VARCHAR(16) NOT NULL PRIMARY KEY," +
                "time INTEGER(16)," +
                "collectedRewards TEXT" +
                ");");

    }

    public TimedPlayer selectOne(String name) {

        return this.sqlExecutor.resultOneQuery(
                "SELECT * FROM " + table + " WHERE name = ?",
                statment -> statment.set(1, name),
                TimedPlayerAdapter.class
        );

    }

    public void insertOne(TimedPlayer timedPlayer) {

        this.sqlExecutor.updateQuery(
                "INSERT INTO " + table + " VALUES(?,?,?);",
                statment -> {
                    statment.set(1, timedPlayer.getName());
                    statment.set(2, timedPlayer.getTimeInServer());
                    statment.set(3, StringUtils.join(",", timedPlayer.getCollectedRewards()));
                }
        );

    }

    public void saveOne(TimedPlayer timedPlayer) {

        this.sqlExecutor.updateQuery(
                "UPDATE " + table + " SET time = ?, collectedRewards = ? WHERE name = ?",
                statment -> {
                    statment.set(1, timedPlayer.getTimeInServer());
                    statment.set(2, StringUtils.join(",", timedPlayer.getCollectedRewards()));
                    statment.set(3, timedPlayer.getName());
                }
        );

    }

}
