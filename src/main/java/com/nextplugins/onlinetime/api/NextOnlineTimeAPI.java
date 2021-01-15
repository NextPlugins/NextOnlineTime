package com.nextplugins.onlinetime.api;

import com.google.inject.Inject;
import com.nextplugins.onlinetime.api.player.TimedPlayer;
import com.nextplugins.onlinetime.api.reward.Reward;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NextOnlineTimeAPI {

    @Getter private static final NextOnlineTimeAPI instance = new NextOnlineTimeAPI();

    @Inject private TimedPlayerManager timedPlayerManager;
    @Inject private RewardManager rewardManager;

    /**
     * Get player time and collected rewards
     *
     * @param name of player
     * @return {@link TimedPlayer} of player found
     */
    public TimedPlayer getUser(String name) {
        return this.timedPlayerManager.getByName(name);
    }

    /**
     * Get reward by name
     *
     * @param name of reward
     * @return {@link Reward} found by name
     */
    public Reward getReward(String name) {
        return this.rewardManager.getByName(name);
    }

    /**
     * Get player by filter
     * Can be used to search players with more x time
     *
     * WARNING:
     * Only from cache.
     * If you want to search with all players, access {@link com.nextplugins.onlinetime.dao.TimedPlayerDAO} in {@link TimedPlayerManager}
     *
     * @param filter custom filter to search
     * @return {@link Optional} with the player found
     */
    public Optional<TimedPlayer> findPlayerByFilter(Predicate<TimedPlayer> filter) {

        return allCachedPlayers().stream()
                .filter(filter)
                .findAny();

    }

    /**
     * Get player cache
     *
     * @return {@link Collection} with all players in cache
     */
    public Collection<TimedPlayer> allCachedPlayers() {
        return this.timedPlayerManager.getPlayers().values();
    }

}
