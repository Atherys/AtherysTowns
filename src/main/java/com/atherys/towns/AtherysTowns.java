package com.atherys.towns;

import com.atherys.towns.listener.PlotterListener;
import com.atherys.towns.persistence.*;
import com.atherys.towns.service.PlottingService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import java.io.IOException;

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

    private static TownsConfig config;

    @Inject
    private Logger logger;

    private TownsDatabase database;

    private ResidentManager residentManager;
    private PlotManager plotManager;
    private TownManager townManager;
    private NationManager nationManager;
    private PlottingService plottingService;
    private RankManager rankManager;
    private PermissionManager permissionManager;

    private void init() {
        instance = this;

        try {
            config = new TownsConfig();
            config.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        database = TownsDatabase.getInstance();
    }

    private void start() {
        plottingService = PlottingService.getInstance();
        Sponge.getEventManager().registerListeners(this, new PlotterListener());
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

    public static ResidentManager getResidentManager() {
        return getInstance().residentManager;
    }

    public static PlotManager getPlotManager() {
        return getInstance().plotManager;
    }

    public static TownManager getTownManager() {
        return getInstance().townManager;
    }

    public static NationManager getNationManager() {
        return getInstance().nationManager;
    }

    public static PlottingService getPlottingService() {
        return getInstance().plottingService;
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static TownsConfig getConfig() {
        return config;
    }

    public static RankManager getRankManager() {
        return getInstance().rankManager;
    }

    public static PermissionManager getPermissionManager() {
        return getInstance().permissionManager;
    }
}
