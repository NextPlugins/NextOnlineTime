package com.nextplugins.onlinetime.models.objects;

import lombok.Data;

import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class AtlasTimedPlayer {

    private final String user;
    private final long loggedInTime;
    private final long totalOnTime;
    private final List<String> prizesCollecteds;

}
