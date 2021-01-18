package com.nextplugins.onlinetime.configuration.values;

import com.nextplugins.onlinetime.NextOnlineTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.Configuration;

import java.util.function.Function;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureValue {

    private static final FeatureValue instance = new FeatureValue();

    private final Configuration configuration = NextOnlineTime.getInstance().getConfig();

    private final boolean useBStats = configuration.getBoolean("bStats.enabled");

    public static <T> T get(Function<FeatureValue, T> supplier) {
        return supplier.apply(FeatureValue.instance);
    }

}
