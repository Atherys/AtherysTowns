package com.atherys.towns;

import javax.inject.Inject;

import com.atherys.core.event.AtherysHibernateConfigurationEvent;
import com.atherys.towns.model.*;
import com.atherys.towns.api.permission.Permissions;
import com.atherys.towns.model.permission.AbstractPermissionContext;
import com.atherys.towns.model.permission.NationPermissionContext;
import com.atherys.towns.model.permission.PlotPermissionContext;
import com.atherys.towns.model.permission.TownPermissionContext;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;

import static com.atherys.towns.AtherysTowns.*;

@Plugin(
        id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore")
        }
)
public class AtherysTowns {

    final static String ID = "atherystowns";
    final static String NAME = "A'therys Towns";
    final static String DESCRIPTION = "A land-management plugin for the Atherys Horizons server";
    final static String VERSION = "1.0.0";

    private static AtherysTowns instance;

    private static boolean init = false;

    private static TownsConfig config;

    @Inject
    private Logger logger;

    private void init() {
        instance = this;

        try {
            config = new TownsConfig();
            config.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
    }

    private void stop() {

    }

    private void onHibernateConfiguration (AtherysHibernateConfigurationEvent event) {
        event.registerEntity(Nation.class);
        event.registerEntity(Town.class);
        event.registerEntity(Plot.class);
        event.registerEntity(Resident.class);

        event.registerEntity(Permissions.class);
        event.registerEntity(AbstractPermissionContext.class);
        event.registerEntity(NationPermissionContext.class);
        event.registerEntity(TownPermissionContext.class);
        event.registerEntity(PlotPermissionContext.class);
    }

    @Listener
    void onInit(GameInitializationEvent event) {
        init();
    }

    @Listener
    void onStart(GameStartingServerEvent event) {
        if (init) start();
    }

    @Listener
    void onStop(GameStoppingServerEvent event) {
        if (init) stop();
    }

    public static AtherysTowns getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static TownsConfig getConfig() {
        return config;
    }
}
