package com.nextplugins.onlinetime.api.conversion;

import com.google.common.collect.Sets;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import lombok.Getter;

import java.util.Set;

@Getter
public abstract class Conversor {

    private final String conversorName;
    private final SQLExecutor executor;

    protected Conversor(String conversorName, SQLExecutor executor) {
        this.conversorName = conversorName;
        this.executor = executor;
    }

    public Set<TimedPlayer> lookupPlayers() {
        return Sets.newLinkedHashSet();
    }

}
