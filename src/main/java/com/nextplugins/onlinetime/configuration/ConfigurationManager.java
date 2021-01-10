package com.nextplugins.onlinetime.configuration;

import com.nextplugins.onlinetime.NextOnlineTime;
import lombok.Data;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data(staticConstructor = "of")
public final class ConfigurationManager {

    private final String config;

    public ConfigurationManager saveDefault() {

        NextOnlineTime instance = NextOnlineTime.getInstance();
        instance.saveResource(this.config, false);

        return this;

    }

    public Configuration load() {

        NextOnlineTime instance = NextOnlineTime.getInstance();

        return YamlConfiguration.loadConfiguration(
                new File(instance.getDataFolder(), this.config)
        );

    }


}
