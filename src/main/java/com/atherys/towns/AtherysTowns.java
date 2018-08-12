package com.atherys.towns;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.permission.PermissionService;

import java.io.IOException;
import java.util.Optional;

import static com.atherys.towns.AtherysTowns.*;

@Plugin(id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "griefprevention"),
                @Dependency(id = "luckperms")
        })
public class AtherysTowns {

    public final static String ID = "atherystowns";
    public final static String NAME = "A'therys Towns";
    public final static String DESCRIPTION = "A custom plugin responsible for agile land management. Created for the A'therys Horizons server.";
    public final static String VERSION = "1.0.0a";

    private static AtherysTowns instance;
    private static boolean init = false;

    @Inject
    private Logger logger;

    private PermissionService permissionService;
    private EconomyService economyService;

    private TownsConfig config;

    public static AtherysTowns getInstance() {
        return instance;
    }

    private void init() {
        instance = this;

        if (!getEconomyService().isPresent()) {
            getLogger().warn("No economy service found. No features relating to economy will function!");
        }

        try {
            config = new TownsConfig("config/" + ID, "config.conf");
            config.init();
        } catch (IOException e) {
            e.printStackTrace();
            init = false;
            return;
        }

//        if (config.DEFAULT) {
//            getLogger()
//                    .warn("Config set to default. Plugin will not initialize further than this.");
//            init = false;
//            return;
//        }

        init = true;
    }

    private void start() {
    }

    private void stop() {
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        init();
    }

    @Listener
    public void onStart(GameStartedServerEvent event) {
        if (init) {
            start();
        }
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        if (init) {
            stop();
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Optional<EconomyService> getEconomyService() {
        return Sponge.getServiceManager().provide(EconomyService.class);
    }

    public Optional<PermissionService> getPermissionService() {
        return Sponge.getServiceManager().provide(PermissionService.class);
    }

    public TownsConfig getConfig() {
        return config;
    }
}
