package com.nextplugins.onlinetime.configuration.values;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.Configuration;

import javax.inject.Named;
import java.util.function.Function;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigValue {

    @Getter private static final ConfigValue instance = new ConfigValue();

    @Inject @Named("main") private Configuration config;

    private final boolean isMySQL = config.getBoolean("connection.mysql.enable");

    // MySQL Values
    private final String address = config.getString("connection.mysql.address");
    private final String username = config.getString("connection.mysql.username");
    private final String password = config.getString("connection.mysql.password");
    private final String database = config.getString("connection.mysql.database");

    // SQLite Values
    private final String file = config.getString("connection.sqlite.file");

    public static <T> T get(Function<ConfigValue, T> supplier) {
        return supplier.apply(ConfigValue.instance);
    }

}


