package com.nextplugins.onlinetime.api.metric;

import lombok.Data;
import org.bukkit.plugin.java.JavaPlugin;

@Data(staticConstructor = "of")
public final class MetricProvider {

    private final JavaPlugin plugin;

    public void register() {

        new MetricsConnector(plugin, 10041);
        plugin.getLogger().info("Métrica de uso habilitada com sucesso.");

    }

}
