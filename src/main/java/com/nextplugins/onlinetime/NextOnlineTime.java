package com.nextplugins.onlinetime;

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
import com.nextplugins.onlinetime.configuration.values.FeatureValue;
import com.nextplugins.onlinetime.configuration.values.MessageValue;
import com.nextplugins.onlinetime.guice.PluginModule;
import com.nextplugins.onlinetime.listener.CheckUseListener;
import com.nextplugins.onlinetime.listener.PlaceholderRegister;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.manager.CheckManager;
import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.parser.ItemParser;
import com.nextplugins.onlinetime.task.TopTimedPlayerTask;
import com.nextplugins.onlinetime.task.UpdatePlayerTimeTask;
import lombok.Getter;
import me.bristermitten.pdm.PluginDependencyManager;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter
public final class NextOnlineTime extends JavaPlugin {

    /**
     * Metrics plugin id (used for statistics)
     */
    private static final int PLUGIN_ID = 10042;

    private Injector injector;
    private SQLConnector sqlConnector;

    /**
     * For duplicates keys, mysql uses: ON DUPLICATE KEY UPDATE ...
     * but, sqlite use: ON CONFLICT(column) DO UPDATE SET ...
     */
    private String duplicateEntry;

    private Configuration messagesConfig;
    private Configuration rewardsConfig;
    private Configuration conversorsConfig;

    @Inject private CheckManager checkManager;
    @Inject private RewardManager rewardManager;
    @Inject private TimedPlayerManager timedPlayerManager;
    @Inject private ConversorManager conversorManager;

    @Inject private TopTimedPlayerTask topTimedPlayerTask;
    @Inject private UpdatePlayerTimeTask updatePlayerTimeTask;

    @Inject private ItemParser itemParser;

    public static NextOnlineTime getInstance() {
        return getPlugin(NextOnlineTime.class);
    }

    @Override
    public void onLoad() {

        this.saveDefaultConfig();
        this.messagesConfig = ConfigurationManager.of("messages.yml").saveDefault().load();
        this.rewardsConfig = ConfigurationManager.of("rewards.yml").saveDefault().load();
        this.conversorsConfig = ConfigurationManager.of("conversors.yml").saveDefault().load();

    }

    @Override
    public void onEnable() {

        PluginDependencyManager.of(this).loadAllDependencies().thenRun(() -> {

            PluginManager pluginManager = Bukkit.getPluginManager();
            try {

                InventoryManager.enable(this);

                this.sqlConnector = configureSqlProvider(this.getConfig());
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

                CheckUseListener checkUseListener = new CheckUseListener(this.timedPlayerManager);

                UserConnectListener userConnectListener = new UserConnectListener(
                        this.timedPlayerManager,
                        this.conversorManager
                );

                pluginManager.registerEvents(checkUseListener, this);
                pluginManager.registerEvents(userConnectListener, this);

                this.getLogger().info("Registered commands and events successfully");

                this.rewardManager.loadRewards();
                this.getLogger().info("Loaded all rewards");

                this.timedPlayerManager.getTimedPlayerDAO().createTable();

                registerTimeUpdaterTask();

                configurePlaceholder(pluginManager);
                configureBStats();

                loadConversors();
                loadCheckItem();
                this.getLogger().info("Loaded info of conversors successfully");

            } catch (Exception exception) {

                exception.printStackTrace();
                this.getLogger().severe("A error occurred on plugin startup, turning off");

                pluginManager.disablePlugin(this);

            }

        });

    }

    private void loadCheckItem() {

        this.checkManager.setCheckItem(this.itemParser.parseSection(
                getConfig().getConfigurationSection("checkItem")
        ));

    }

    @Override
    public void onDisable() {

        Bukkit.getOnlinePlayers().forEach(this.timedPlayerManager::purge);

    }

    private void loadConversors() {

        String atlasConversor = "AtlasTempoOnline";
        if (conversorsConfig.getBoolean(atlasConversor + ".use")) {

            ConfigurationSection section = conversorsConfig.getConfigurationSection(atlasConversor);
            SQLConnector connector = this.configureSqlProvider(section);

            AtlasOnlineTimeConversor conversor = new AtlasOnlineTimeConversor(
                    atlasConversor,
                    section.getString("connection.table"),
                    connector
            );

            this.conversorManager.registerConversor(conversor);

        }

        String onlineTimePlusConversor = "OnlineTimePlus";
        if (conversorsConfig.getBoolean(onlineTimePlusConversor + ".use")) {

            ConfigurationSection section = conversorsConfig.getConfigurationSection(onlineTimePlusConversor);
            SQLConnector connector = this.configureSqlProvider(section);

            OnlineTimePlusConversor conversor = new OnlineTimePlusConversor(
                    onlineTimePlusConversor,
                    section.getString("connection.table"),
                    connector
            );

            this.conversorManager.registerConversor(conversor);

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

            this.duplicateEntry = "INSERT INTO %s VALUES(?,?,?) ON DUPLICATE KEY UPDATE time = ?, collectedRewards = ?";

        } else {

            ConfigurationSection sqliteSection = section.getConfigurationSection("connection.sqlite");

            connector = SQLiteDatabaseType.builder()
                    .file(new File(sqliteSection.getString("file")))
                    .build()
                    .connect();

            this.duplicateEntry = "INSERT OR REPLACE INTO %s VALUES(?,?,?)";

        }

        return connector;

    }

    private void configurePlaceholder(PluginManager pluginManager) {

        if (!pluginManager.isPluginEnabled("PlaceholderAPI")) return;

        PlaceholderRegister.of(this).register();
        this.getLogger().info("Bind with PlaceholderAPI successfully");

    }

    private void registerTimeUpdaterTask() {

        BukkitScheduler scheduler = Bukkit.getScheduler();

        int updaterTime = this.getConfig().getInt("updaterTime");
        TimeUnit timeFormat = this.parseTime(this.getConfig().getString("timeFormat"));

        long updateTimeInTicks = timeFormat.toSeconds(updaterTime) * 20;

        scheduler.runTaskTimerAsynchronously(
                this,
                this.updatePlayerTimeTask,
                updateTimeInTicks,
                updateTimeInTicks
        );

        scheduler.runTaskTimerAsynchronously(
                this,
                this.topTimedPlayerTask,
                0,
                30 * 60 * 20L
        );

    }

    private TimeUnit parseTime(String string) {

        TimeUnit timeUnit = TimeUnit.valueOf(string);

        if (timeUnit != TimeUnit.HOURS && timeUnit != TimeUnit.MINUTES) return TimeUnit.MINUTES;
        return timeUnit;

    }

    private void configureBStats() {
        if (!FeatureValue.get(FeatureValue::useBStats)) return;

        new Metrics(this, PLUGIN_ID);
        this.getLogger().info("Enabled bStats successfully, statistics enabled");

    }

}
