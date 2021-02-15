package com.nextplugins.onlinetime.models.comparators;

import com.nextplugins.onlinetime.api.reward.Reward;

import java.util.Comparator;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class RewardComparator implements Comparator<Reward> {

    @Override
    public int compare(Reward o1, Reward o2) {
        return Long.compare(o1.getTime(), o2.getTime());
    }

}
