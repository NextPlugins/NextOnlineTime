package com.nextplugins.onlinetime.api.models.enums;

import com.nextplugins.onlinetime.configuration.values.MessageValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@AllArgsConstructor
public enum RewardStatus {

    CAN_COLLECT(0, MessageValue.get(MessageValue::canCollect), true),
    NO_TIME(1, MessageValue.get(MessageValue::noTimeToCollect), false),
    COLLECTED(2, MessageValue.get(MessageValue::alreadyCollected), false);
    private final int code;
    private final String message;
    private final boolean canCollect;

}
