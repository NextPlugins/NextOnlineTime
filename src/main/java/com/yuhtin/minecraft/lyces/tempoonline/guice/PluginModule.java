package com.yuhtin.minecraft.lyces.tempoonline.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.yuhtin.minecraft.lyces.tempoonline.TempoOnline;
import com.yuhtin.minecraft.lyces.tempoonline.configuration.values.ConfigValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.Configuration;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = false)
@Data(staticConstructor = "from")
public class PluginModule extends AbstractModule {

    private final TempoOnline tempoOnline;

    @Override
    protected void configure() {

        bind(TempoOnline.class)
                .toInstance(tempoOnline);

        bind(Logger.class)
                .annotatedWith(Names.named("main"))
                .toInstance(tempoOnline.getLogger());

        bind(Configuration.class)
                .annotatedWith(Names.named("main"))
                .toInstance(tempoOnline.getConfig());

        bind(Configuration.class)
                .annotatedWith(Names.named("messages"))
                .toInstance(tempoOnline.getMessagesConfig());

        bind(Configuration.class)
                .annotatedWith(Names.named("rewards"))
                .toInstance(tempoOnline.getRewadsConfig());

        bind(SQLProvider.class)
                .toInstance(tempoOnline.getSqlProvider());

    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

}

