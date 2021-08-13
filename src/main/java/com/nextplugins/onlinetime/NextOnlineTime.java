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
import com.nextplugins.onlinetime.listener.CheckUseListener;
import com.nextplugins.onlinetime.listener.PlaceholderRegister;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.manager.*;
import com.nextplugins.onlinetime.npc.manager.NPCManager;
import com.nextplugins.onlinetime.npc.runnable.NPCRunnable;
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
import java.util.logging.Level;

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
    private FileConfiguration npcConfig;

    private TimedPlayerDAO timedPlayerDAO;

    private NPCManager npcManager;
    private CheckManager checkManager;
    private RewardManager rewardManager;
    private InventoryRegistry inventoryRegistry;
    private ConversorManager conversorManager;
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
        npcConfig = ConfigurationManager.of("npc.yml").saveDefault().load();

    }

    @Override
    public void onEnable() {

        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();
        val pluginManager = Bukkit.getPluginManager();

        InventoryManager.enable(this);

        sqlConnector = configureSqlProvider(getConfig());
        timedPlayerDAO = new TimedPlayerDAO(new SQLExecutor(sqlConnector));

        npcManager = new NPCManager();
        rewardManager = new RewardManager();
        inventoryRegistry = new InventoryRegistry();
        conversorManager = new ConversorManager(timedPlayerDAO);
        timedPlayerManager = new TimedPlayerManager(timedPlayerDAO);
        checkManager = new CheckManager();
        topTimedPlayerManager = new TopTimedPlayerManager(timedPlayerDAO);
        updatePlayerTimeTask = new UpdatePlayerTimeTask(timedPlayerManager);

        itemParser = new ItemParser();

        rewardManager.loadRewards();
        timedPlayerDAO.createTable();

        checkManager.init();
        inventoryRegistry.init();
        npcManager.init();

        configurePlaceholder(pluginManager);

        loadConversors();
        loadCheckItem();

        registerTopUpdaterTask();

        getCommand("tempo").setExecutor(new OnlineTimeCommand());

        val checkUseListener = new CheckUseListener(timedPlayerManager);
        val userConnectListener = new UserConnectListener(
            timedPlayerManager,
            conversorManager
        );

        pluginManager.registerEvents(checkUseListener, this);
        pluginManager.registerEvents(userConnectListener, this);

        MetricProvider.of(this).register();

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);

    }

    @Override
    public void onDisable() {

        Bukkit.getOnlinePlayers().forEach(timedPlayerManager::purge);

        if (npcManager.isEnabled()) {

            NPCRunnable runnable = (NPCRunnable) npcManager.getRunnable();
            runnable.despawn();

        }

    }

    private void loadCheckItem() {

        checkManager.setCheckItem(itemParser.parseSection(
            getConfig().getConfigurationSection("checkItem")
        ));

    }

    private void loadConversors() {

        val atlasConversor = "AtlasTempoOnline";
        if (conversorsConfig.getBoolean(atlasConversor + ".use")) {

            val section = conversorsConfig.getConfigurationSection(atlasConversor);
            val connector = configureSqlProvider(section);

            val conversor = new AtlasOnlineTimeConversor(
                atlasConversor,
                section.getString("connection.table"),
                connector
            );

            conversorManager.registerConversor(conversor);

        }

        val onlineTimePlusConversor = "OnlineTimePlus";
        if (conversorsConfig.getBoolean(onlineTimePlusConversor + ".use")) {

            val section = conversorsConfig.getConfigurationSection(onlineTimePlusConversor);
            val connector = configureSqlProvider(section);

            val conversor = new OnlineTimePlusConversor(
                onlineTimePlusConversor,
                section.getString("connection.table"),
                connector
            );

            conversorManager.registerConversor(conversor);

        }

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
