package com.atherys.towns;

import static com.atherys.towns.AtherysTowns.DESCRIPTION;
import static com.atherys.towns.AtherysTowns.ID;
import static com.atherys.towns.AtherysTowns.NAME;
import static com.atherys.towns.AtherysTowns.VERSION;

import com.atherys.towns.listener.PlotterListener;
import com.atherys.towns.service.PlottingService;
import com.atherys.towns.persistence.NationManager;
import com.atherys.towns.persistence.PlotManager;
import com.atherys.towns.persistence.ResidentManager;
import com.atherys.towns.persistence.TownManager;
import com.atherys.towns.persistence.TownsDatabase;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

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

    private ResidentManager residentManager;
    private PlotManager plotManager;
    private TownManager townManager;
    private NationManager nationManager;
    private PlottingService plottingService;

    private void init() {
        instance = this;

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
}
