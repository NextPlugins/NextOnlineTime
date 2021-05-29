package com.nextplugins.onlinetime;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.nextplugins.onlinetime.api.conversion.impl.atlas.AtlasOnlineTimeConversor;
import com.nextplugins.onlinetime.api.conversion.impl.victor.OnlineTimePlusConversor;
import com.nextplugins.onlinetime.command.OnlineTimeCommand;
import com.nextplugins.onlinetime.configuration.ConfigurationManager;
import com.nextplugins.onlinetime.guice.PluginModule;
import com.nextplugins.onlinetime.listener.CheckUseListener;
import com.nextplugins.onlinetime.listener.PlaceholderRegister;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.manager.CheckManager;
import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.npc.manager.NPCManager;
import com.nextplugins.onlinetime.npc.runnable.NPCRunnable;
import com.nextplugins.onlinetime.parser.ItemParser;
import com.nextplugins.onlinetime.registry.InventoryRegistry;
import com.nextplugins.onlinetime.task.TopTimedPlayerTask;
import com.nextplugins.onlinetime.task.UpdatePlayerTimeTask;
import lombok.Getter;
import lombok.val;
import me.bristermitten.pdm.PluginDependencyManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Getter
public final class NextOnlineTime extends JavaPlugin {

    /**
     * Metrics plugin id (used for statistics)
     */
    private static final int PLUGIN_ID = 10042;

    private Injector injector;
    private SQLConnector sqlConnector;

    private FileConfiguration messagesConfig;
    private FileConfiguration rewardsConfig;
    private FileConfiguration conversorsConfig;
    private FileConfiguration npcConfig;

    @Inject private CheckManager checkManager;
    @Inject private RewardManager rewardManager;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private ConversorManager conversorManager;
    @Inject private TimedPlayerManager timedPlayerManager;
    @Inject private NPCManager npcManager;

    @Inject private TopTimedPlayerTask topTimedPlayerTask;
    @Inject private UpdatePlayerTimeTask updatePlayerTimeTask;

    @Inject private ItemParser itemParser;

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

        getLogger().info("Baixando e carregando dependências necessárias...");

        val downloadTime = Stopwatch.createStarted();

        PluginDependencyManager.of(this)
                .loadAllDependencies()
                .exceptionally(throwable -> {

                    throwable.printStackTrace();

                    getLogger().severe("Ocorreu um erro durante a inicialização do plugin!");
                    Bukkit.getPluginManager().disablePlugin(this);

                    return null;

                })
                .join();

        downloadTime.stop();

        getLogger().log(Level.INFO, "Dependências carregadas com sucesso! ({0})", downloadTime);
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();
        val pluginManager = Bukkit.getPluginManager();

        InventoryManager.enable(this);

        sqlConnector = configureSqlProvider(getConfig());

        injector = PluginModule.from(this).createInjector();
        injector.injectMembers(this);

        getCommand("tempo").setExecutor(injector.getInstance(OnlineTimeCommand.class));

        CheckUseListener checkUseListener = new CheckUseListener(timedPlayerManager);

        UserConnectListener userConnectListener = new UserConnectListener(
                timedPlayerManager,
                conversorManager
        );

        pluginManager.registerEvents(checkUseListener, this);
        pluginManager.registerEvents(userConnectListener, this);

        rewardManager.loadRewards();
        timedPlayerManager.getTimedPlayerDAO().createTable();

        checkManager.init();
        inventoryRegistry.init();
        npcManager.init();

        configurePlaceholder(pluginManager);
        configureBStats();

        loadConversors();
        loadCheckItem();

        registerTopUpdaterTask();

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

        String atlasConversor = "AtlasTempoOnline";
        if (conversorsConfig.getBoolean(atlasConversor + ".use")) {

            ConfigurationSection section = conversorsConfig.getConfigurationSection(atlasConversor);
            SQLConnector connector = configureSqlProvider(section);

            AtlasOnlineTimeConversor conversor = new AtlasOnlineTimeConversor(
                    atlasConversor,
                    section.getString("connection.table"),
                    connector
            );

            conversorManager.registerConversor(conversor);

        }

        String onlineTimePlusConversor = "OnlineTimePlus";
        if (conversorsConfig.getBoolean(onlineTimePlusConversor + ".use")) {

            ConfigurationSection section = conversorsConfig.getConfigurationSection(onlineTimePlusConversor);
            SQLConnector connector = configureSqlProvider(section);

            OnlineTimePlusConversor conversor = new OnlineTimePlusConversor(
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

            ConfigurationSection mysqlSection = section.getConfigurationSection("connection.mysql");

            connector = MySQLDatabaseType.builder()
                    .address(mysqlSection.getString("address"))
                    .username(mysqlSection.getString("username"))
                    .password(mysqlSection.getString("password"))
                    .database(mysqlSection.getString("database"))
                    .build()
                    .connect();

        } else {

            ConfigurationSection sqliteSection = section.getConfigurationSection("connection.sqlite");

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

        BukkitScheduler scheduler = Bukkit.getScheduler();

        int updaterTime = getConfig().getInt("updaterTime");
        TimeUnit timeFormat = parseTime(getConfig().getString("timeFormat"));

        long updateTimeInTicks = timeFormat.toSeconds(updaterTime) * 20;

        scheduler.runTaskTimerAsynchronously(
                this,
                updatePlayerTimeTask,
                updateTimeInTicks,
                updateTimeInTicks
        );

        scheduler.runTaskTimerAsynchronously(
                this,
                topTimedPlayerTask,
                0, 30 * 60 * 20L
        );

    }

    private TimeUnit parseTime(String string) {

        TimeUnit timeUnit = TimeUnit.valueOf(string);

        if (timeUnit != TimeUnit.HOURS && timeUnit != TimeUnit.MINUTES) return TimeUnit.MINUTES;
        return timeUnit;

    }

    private void configureBStats() {

        Metrics metrics = new Metrics(this, PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_rewards_registered",
                () -> rewardManager.getRewards().size())
        );

        getLogger().info("Enabled bStats successfully, statistics enabled");

    }

}
