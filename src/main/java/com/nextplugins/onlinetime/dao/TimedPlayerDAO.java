package com.nextplugins.onlinetime.dao;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.dao.adapter.TimedPlayerAdapter;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public final class TimedPlayerDAO {

    private static final String TABLE = "onlinetime_players";

    private final SQLExecutor sqlExecutor;

    public void createTable() {

        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "name VARCHAR(16) NOT NULL PRIMARY KEY UNIQUE," +
                "time INTEGER(8)," +
                "collectedRewards TEXT" +
                ");");

    }

    public TimedPlayer selectOne(String name) {

        return sqlExecutor.resultOneQuery(
                "SELECT * FROM " + TABLE + " WHERE name = ?",
                statment -> statment.set(1, name),
                TimedPlayerAdapter.class
        );

    }

    public Set<TimedPlayer> selectAll(String preferences) {

        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE + " " + preferences,
                statement -> {
                },
                TimedPlayerAdapter.class
        );

    }

    public void saveOne(TimedPlayer timedPlayer) {

        sqlExecutor.updateQuery(
                String.format("REPLACE INTO %s VALUES(?,?,?)", TABLE),
                statement -> {

                    statement.set(1, timedPlayer.getName());
                    statement.set(2, timedPlayer.getTimeInServer());
                    statement.set(3, String.join(",", timedPlayer.getCollectedRewards()));

                }
        );

    }

}
