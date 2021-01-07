package com.nextplugins.onlinetime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.onlinetime.NextOnlineTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.Configuration;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = false)
@Data(staticConstructor = "from")
public class PluginModule extends AbstractModule {

    private final NextOnlineTime nextOnlineTime;

    @Override
    protected void configure() {

        bind(NextOnlineTime.class)
                .toInstance(nextOnlineTime);

        bind(Logger.class)
                .annotatedWith(Names.named("main"))
                .toInstance(nextOnlineTime.getLogger());

        bind(Configuration.class)
                .annotatedWith(Names.named("main"))
                .toInstance(nextOnlineTime.getConfig());

        bind(Configuration.class)
                .annotatedWith(Names.named("messages"))
                .toInstance(nextOnlineTime.getMessagesConfig());

        bind(Configuration.class)
                .annotatedWith(Names.named("rewards"))
                .toInstance(nextOnlineTime.getRewadsConfig());

        bind(SQLExecutor.class)
                .toInstance(new SQLExecutor(nextOnlineTime.getSqlConnector()));

    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

}

