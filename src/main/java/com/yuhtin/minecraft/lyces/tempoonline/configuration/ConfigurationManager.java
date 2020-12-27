package com.yuhtin.minecraft.lyces.tempoonline.configuration;

import com.yuhtin.minecraft.lyces.tempoonline.TempoOnline;
import lombok.Data;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data(staticConstructor = "of")
public class ConfigurationManager {

    private final String config;

    public ConfigurationManager saveDefault() {

        TempoOnline instance = TempoOnline.getInstance();
        instance.saveResource(config, false);

        return this;

    }

    public Configuration load() {

        return YamlConfiguration.loadConfiguration(
                new File(config)
        );

    }


}
