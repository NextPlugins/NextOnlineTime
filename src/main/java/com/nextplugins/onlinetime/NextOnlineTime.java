package com.nextplugins.onlinetime;

import com.google.common.base.Stopwatch;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.api.conversion.impl.atlas.AtlasOnlineTimeConversor;
import com.nextplugins.onlinetime.api.conversion.impl.victor.OnlineTimePlusConversor;
import com.nextplugins.onlinetime.api.metric.MetricProvider;
import com.nextplugins.onlinetime.command.OnlineTimeCommand;
import com.nextplugins.onlinetime.configuration.ConfigurationManager;
import com.nextplugins.onlinetime.dao.TimedPlayerDAO;
import com.nextplugins.onlinetime.listener.PlaceholderRegister;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.manager.*;
import com.nextplugins.onlinetime.parser.ItemParser;
import com.nextplugins.onlinetime.registry.InventoryRegistry;
import com.nextplugins.onlinetime.task.UpdatePlayerTimeTask;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter
public final class NextOnlineTime extends JavaPlugin {

    /**
     * Metrics plugin id (used for statistics)
     */
    private static final int PLUGIN_ID = 10042;

    private SQLConnector sqlConnector;

    private FileConfiguration messagesConfig;
    private FileConfiguration rewardsConfig;
    private FileConfiguration conversorsConfig;

    private TimedPlayerDAO timedPlayerDAO;

    private RewardManager rewardManager;
    private InventoryRegistry inventoryRegistry;
    private TimedPlayerManager timedPlayerManager;
    private TopTimedPlayerManager topTimedPlayerManager;

    private UpdatePlayerTimeTask updatePlayerTimeTask;

    private ItemParser itemParser;

    public static NextOnlineTime getInstance() {
        return getPlugin(NextOnlineTime.class);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();

        messagesConfig = ConfigurationManager.of("messages.yml").saveDefault().load();
        rewardsConfig = ConfigurationManager.of("rewards.yml").saveDefault().load();
        conversorsConfig = ConfigurationManager.of("conversors.yml").saveDefault().load();
    }

    @Override
    public void onEnable() {
        val loadTime = Stopwatch.createStarted();
        val pluginManager = Bukkit.getPluginManager();

        InventoryManager.enable(this);

        sqlConnector = configureSqlProvider(getConfig());
        timedPlayerDAO = new TimedPlayerDAO(new SQLExecutor(sqlConnector));

        rewardManager = new RewardManager();
        inventoryRegistry = new InventoryRegistry();
        timedPlayerManager = new TimedPlayerManager(timedPlayerDAO);
        topTimedPlayerManager = new TopTimedPlayerManager(timedPlayerDAO);
        updatePlayerTimeTask = new UpdatePlayerTimeTask(timedPlayerManager);

        itemParser = new ItemParser();

        rewardManager.loadRewards();
        timedPlayerDAO.createTable();

        inventoryRegistry.init();

        configurePlaceholder(pluginManager);
        registerTopUpdaterTask();

        getCommand("tempo").setExecutor(new OnlineTimeCommand());

        val userConnectListener = new UserConnectListener(timedPlayerManager);

        pluginManager.registerEvents(userConnectListener, this);

        MetricProvider.of(this).register();

        loadTime.stop();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(timedPlayerManager::purge);
    }

    private SQLConnector configureSqlProvider(ConfigurationSection section) {
        SQLConnector connector;
        if (section.getBoolean("connection.mysql.enable")) {
            val mysqlSection = section.getConfigurationSection("connection.mysql");

            connector = MySQLDatabaseType.builder()
                .address(mysqlSection.getString("address"))
                .username(mysqlSection.getString("username"))
                .password(mysqlSection.getString("password"))
                .database(mysqlSection.getString("database"))
                .build()
                .connect();
        } else {
            val sqliteSection = section.getConfigurationSection("connection.sqlite");

            connector = SQLiteDatabaseType.builder()
                .file(new File(sqliteSection.getString("file")))
                .build()
                .connect();
        }

        return connector;
    }

    private void configurePlaceholder(PluginManager pluginManager) {
        if (!pluginManager.isPluginEnabled("PlaceholderAPI")) return;

        PlaceholderRegister.of(this).register();
        getLogger().info("Bind with PlaceholderAPI successfully");
    }

    private void registerTopUpdaterTask() {
        val scheduler = Bukkit.getScheduler();
        val updaterTime = getConfig().getInt("updaterTime");
        val timeFormat = parseTime(getConfig().getString("timeFormat"));
        val updateTimeInTicks = timeFormat.toSeconds(updaterTime) * 20;

        scheduler.runTaskTimerAsynchronously(
            this,
            updatePlayerTimeTask,
            updateTimeInTicks,
            updateTimeInTicks
        );
    }

    private TimeUnit parseTime(String string) {
        val timeUnit = TimeUnit.valueOf(string);

        if (timeUnit != TimeUnit.HOURS && timeUnit != TimeUnit.MINUTES) return TimeUnit.MINUTES;
        return timeUnit;
    }

}
