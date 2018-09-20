package com.atherys.towns;

import com.atherys.towns.persistence.TownsDatabase;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;

import static com.atherys.towns.AtherysTowns.*;

@Plugin(
        id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "luckperms")
        }
)
public class AtherysTowns {

    final static String ID = "atherystowns";
    final static String NAME = "A'therys Towns";
    final static String DESCRIPTION = "A land-management plugin for the Atherys Horizons server";
    final static String VERSION = "1.0.0";

    private static AtherysTowns instance;

    private static boolean init = false;

    @Inject
    private Logger logger;

    private TownsDatabase database;

    private void init() {
        instance = this;

        database = TownsDatabase.getInstance();
    }

    private void start() {

    }

    private void stop() {

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

    public static TownsDatabase getDatabase() {
        return getInstance().database;
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }
}
