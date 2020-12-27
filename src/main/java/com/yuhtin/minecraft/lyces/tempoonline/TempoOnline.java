package com.yuhtin.minecraft.lyces.tempoonline;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TempoOnline extends JavaPlugin {

    private Injector injector;

    @Override
    public void onEnable() {

        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                bind(TempoOnline.class).toInstance(JavaPlugin.getPlugin(TempoOnline.class));
                bind(Logger.class).annotatedWith(Names.named("main")).toInstance(getLogger());


            }
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
