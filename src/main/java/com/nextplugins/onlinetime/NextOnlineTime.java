package com.nextplugins.onlinetime;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.henryfabio.sqlprovider.mysql.MySQLProvider;
import com.henryfabio.sqlprovider.mysql.configuration.MySQLConfiguration;
import com.henryfabio.sqlprovider.sqlite.SQLiteProvider;
import com.henryfabio.sqlprovider.sqlite.configuration.SQLiteConfiguration;
import com.nextplugins.onlinetime.command.OnlineTimeCommand;
import com.nextplugins.onlinetime.configuration.ConfigurationManager;
import com.nextplugins.onlinetime.configuration.values.ConfigValue;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.guice.PluginModule;
import com.nextplugins.onlinetime.manager.RewardManager;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class NextOnlineTime extends JavaPlugin {

    private Injector injector;
    private SQLProvider sqlProvider;

    private Configuration messagesConfig;
    private Configuration rewadsConfig;

    @Inject
    private RewardManager rewardManager;

    public static NextOnlineTime getInstance() {
        return getPlugin(NextOnlineTime.class);
    }

    @Override
    public void onLoad() {

        this.saveDefaultConfig();
        this.messagesConfig = ConfigurationManager.of("messages.yml").saveDefault().load();
        this.rewadsConfig = ConfigurationManager.of("rewards.yml").saveDefault().load();

    }

    @Override
    public void onEnable() {

        try {

            configureSqlProvider();
            this.sqlProvider.connect();

            this.getLogger().info("Connection with sql successfully");

            this.injector = PluginModule.from(this).createInjector();
            this.injector.injectMembers(this);

            this.injector.injectMembers(ConfigValue.instance());
            this.injector.injectMembers(MessageValue.instance());

            this.getLogger().info("Guice injection successfully");

            BukkitFrame bukkitFrame = new BukkitFrame(this);
            bukkitFrame.registerCommands(
                    this.injector.getInstance(OnlineTimeCommand.class)
            );

            this.getLogger().info("Registered commands successfully");

            this.rewardManager.loadRewards();
            this.getLogger().info("Loaded all rewards");

        } catch (Exception exception) {

            exception.printStackTrace();
            this.getLogger().severe("A error occurred on plugin startup, turning off");

            Bukkit.getPluginManager().disablePlugin(this);

        }


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