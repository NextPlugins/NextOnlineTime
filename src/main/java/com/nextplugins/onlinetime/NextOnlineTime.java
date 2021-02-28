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
import com.nextplugins.onlinetime.listener.InteractNPCListener;
import com.nextplugins.onlinetime.listener.PlaceholderRegister;
import com.nextplugins.onlinetime.listener.UserConnectListener;
import com.nextplugins.onlinetime.manager.CheckManager;
import com.nextplugins.onlinetime.manager.ConversorManager;
import com.nextplugins.onlinetime.manager.RewardManager;
import com.nextplugins.onlinetime.manager.TimedPlayerManager;
import com.nextplugins.onlinetime.manager.NPCManager;
import com.nextplugins.onlinetime.parser.ItemParser;
import com.nextplugins.onlinetime.registry.InventoryRegistry;
import com.nextplugins.onlinetime.task.TopTimedPlayerTask;
import com.nextplugins.onlinetime.task.UpdatePlayerTimeTask;
import lombok.Getter;
import me.bristermitten.pdm.PluginDependencyManager;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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

        this.saveDefaultConfig();
        this.messagesConfig = ConfigurationManager.of("messages.yml").saveDefault().load();
        this.rewardsConfig = ConfigurationManager.of("rewards.yml").saveDefault().load();
        this.conversorsConfig = ConfigurationManager.of("conversors.yml").saveDefault().load();
        this.npcConfig = ConfigurationManager.of("npc.yml").saveDefault().load();

    }

    @Override
    public void onEnable() {

        PluginDependencyManager.of(this).loadAllDependencies().thenRun(() -> {

            PluginManager pluginManager = Bukkit.getPluginManager();
            try {

                InventoryManager.enable(this);

                this.sqlConnector = configureSqlProvider(this.getConfig());

                this.injector = PluginModule.from(this).createInjector();
                this.injector.injectMembers(this);

                BukkitFrame bukkitFrame = new BukkitFrame(this);
                bukkitFrame.registerCommands(
                        this.injector.getInstance(OnlineTimeCommand.class)
                );

                bukkitFrame.getMessageHolder().setMessage(
                        MessageType.INCORRECT_USAGE,
                        MessageValue.get(MessageValue::incorrectUsage)
                );

                CheckUseListener checkUseListener = new CheckUseListener(this.timedPlayerManager);
                InteractNPCListener interactNPCListener = new InteractNPCListener(this.npcManager);

                UserConnectListener userConnectListener = new UserConnectListener(
                        this.timedPlayerManager,
                        this.conversorManager
                );

                pluginManager.registerEvents(checkUseListener, this);
                pluginManager.registerEvents(interactNPCListener, this);
                pluginManager.registerEvents(userConnectListener, this);

                this.rewardManager.loadRewards();
                this.timedPlayerManager.getTimedPlayerDAO().createTable();

                this.checkManager.init();
                this.inventoryRegistry.init();
                this.npcManager.init();

                configurePlaceholder(pluginManager);
                configureBStats();

                loadConversors();
                loadCheckItem();

                registerTopUpdaterTask();

                this.getLogger().info("Plugin loaded successfully");

            } catch (Exception exception) {

                exception.printStackTrace();
                this.getLogger().severe("A error occurred on plugin startup, turning off");

                pluginManager.disablePlugin(this);

            }

        });

    }

    @Override
    public void onDisable() {

        Bukkit.getOnlinePlayers().forEach(this.timedPlayerManager::purge);
        this.npcManager.despawn();

    }

    private void loadCheckItem() {

        this.checkManager.setCheckItem(this.itemParser.parseSection(
                getConfig().getConfigurationSection("checkItem")
        ));

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
        this.getLogger().info("Bind with PlaceholderAPI successfully");

    }

    private void registerTopUpdaterTask() {

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

        Metrics metrics = new Metrics(this, PLUGIN_ID);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_rewards_registered",
                () -> this.rewardManager.getRewards().size())
        );

        this.getLogger().info("Enabled bStats successfully, statistics enabled");

    }

}
