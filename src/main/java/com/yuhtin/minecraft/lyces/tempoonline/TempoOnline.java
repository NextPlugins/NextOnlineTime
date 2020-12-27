package com.yuhtin.minecraft.lyces.tempoonline;

import com.google.inject.Injector;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.henryfabio.sqlprovider.mysql.MySQLProvider;
import com.henryfabio.sqlprovider.mysql.configuration.MySQLConfiguration;
import com.henryfabio.sqlprovider.sqlite.SQLiteProvider;
import com.henryfabio.sqlprovider.sqlite.configuration.SQLiteConfiguration;
import com.yuhtin.minecraft.lyces.tempoonline.guice.PluginModule;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TempoOnline extends JavaPlugin {

    private Injector injector;

    @Getter private SQLProvider sqlProvider;

    public static TempoOnline getInstance() {
        return getPlugin(TempoOnline.class);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {

        configureSqlProvider();
        this.sqlProvider.connect();

        this.getLogger().info("Connection with sql successfully");

        this.injector = PluginModule.from(this).createInjector();
        this.injector.injectMembers(this);

        this.getLogger().info("Guice injection successfully");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void configureSqlProvider() {

        FileConfiguration configuration = getConfig();
        if (configuration.getBoolean("connection.mysql.enable")) {

            ConfigurationSection mysqlSection = configuration.getConfigurationSection("connection.mysql");

            sqlProvider = new MySQLProvider(new MySQLConfiguration()
                    .address(mysqlSection.getString("address"))
                    .username(mysqlSection.getString("username"))
                    .password(mysqlSection.getString("password"))
                    .database(mysqlSection.getString("database"))
            );

        } else {

            ConfigurationSection sqliteSection = configuration.getConfigurationSection("connection.sqlite");

            sqlProvider = new SQLiteProvider(new SQLiteConfiguration()
                    .file(new File(
                            this.getDataFolder(),
                            sqliteSection.getString("file")
                    ))
            );

        }

    }
}
