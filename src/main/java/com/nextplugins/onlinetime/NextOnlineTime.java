package com.nextplugins.onlinetime;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.nextplugins.onlinetime.command.OnlineTimeCommand;
import com.nextplugins.onlinetime.configuration.ConfigurationManager;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.guice.PluginModule;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.task.UpdatePlayerTimeTask;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter
public final class NextOnlineTime extends JavaPlugin {

    private Injector injector;
    private SQLConnector sqlConnector;

    private Configuration messagesConfig;
    private Configuration rewadsConfig;

    private UpdatePlayerTimeTask updatePlayerTimeTask;

    @Inject private RewardManager rewardManager;
    @Inject private TimedPlayerManager timedPlayerManager;

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

            InventoryManager.enable(this);

            configureSqlProvider();
            this.getLogger().info("Connection with sql successfully");

            this.injector = PluginModule.from(this).createInjector();
            this.injector.injectMembers(this);

            this.getLogger().info("Guice injection successfully");

            BukkitFrame bukkitFrame = new BukkitFrame(this);
            bukkitFrame.registerCommands(
                    this.injector.getInstance(OnlineTimeCommand.class)
            );

            bukkitFrame.getMessageHolder().setMessage(
                    MessageType.INCORRECT_USAGE,
                    MessageValue.get(MessageValue::incorrectUsage)
            );

            this.getLogger().info("Registered commands successfully");

            this.rewardManager.loadRewards();
            this.getLogger().info("Loaded all rewards");

            this.timedPlayerManager.getTimedPlayerDAO().createTable();

            registerTimeUpdaterTask();

        } catch (Exception exception) {

            exception.printStackTrace();
            this.getLogger().severe("A error occurred on plugin startup, turning off");

            Bukkit.getPluginManager().disablePlugin(this);

        }

    }

    @Override
    public void onDisable() {

        this.updatePlayerTimeTask.run();
        Bukkit.getOnlinePlayers().forEach(this.timedPlayerManager::purge);

    }

    private void configureSqlProvider() {

        FileConfiguration configuration = getConfig();
        if (configuration.getBoolean("connection.mysql.enable")) {

            ConfigurationSection mysqlSection = configuration.getConfigurationSection("connection.mysql");

            sqlConnector = MySQLDatabaseType.builder()
                    .address(mysqlSection.getString("address"))
                    .username(mysqlSection.getString("username"))
                    .password(mysqlSection.getString("password"))
                    .database(mysqlSection.getString("database"))
                    .build()
                    .connect();

        } else {

            ConfigurationSection sqliteSection = configuration.getConfigurationSection("connection.sqlite");

            sqlConnector = SQLiteDatabaseType.builder()
                    .file(new File(
                            this.getDataFolder(),
                            sqliteSection.getString("file")
                    ))
                    .build()
                    .connect();

        }

    }

    private void registerTimeUpdaterTask() {

        int updaterTime = this.getConfig().getInt("updaterTime");
        TimeUnit timeFormat = this.parseTime(this.getConfig().getString("timeFormat"));

        long updateTimeInTicks = timeFormat.toSeconds(updaterTime) * 20;

        this.updatePlayerTimeTask = new UpdatePlayerTimeTask(
                updaterTime,
                timeFormat,
                timedPlayerManager
        );

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                updatePlayerTimeTask,
                updateTimeInTicks,
                updateTimeInTicks
        );

    }

    private TimeUnit parseTime(String string) {

        TimeUnit timeUnit = TimeUnit.valueOf(string);

        if (timeUnit != TimeUnit.HOURS && timeUnit != TimeUnit.MINUTES) return TimeUnit.MINUTES;
        return timeUnit;

    }
}
