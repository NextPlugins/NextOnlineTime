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
import java.util.Set;
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

    public Optional<TimedPlayer> findPlayerByFilter(Predicate<TimedPlayer> filter) {

        return allCachedPlayers().stream()
                .filter(filter)
                .findAny();

    }

    public Collection<TimedPlayer> allCachedPlayers() {
        return this.timedPlayerManager.getPlayers().values();
    }

}
