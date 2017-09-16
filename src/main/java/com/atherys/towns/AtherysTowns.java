package com.atherys.towns;

import com.atherys.towns.base.TownsObject;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.commands.nation.NationMasterCommand;
import com.atherys.towns.commands.plot.PlotMasterCommand;
import com.atherys.towns.commands.resident.ResidentCommand;
import com.atherys.towns.commands.town.TownMasterCommand;
import com.atherys.towns.db.DatabaseManager;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.listeners.PlayerListeners;
import com.atherys.towns.managers.*;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.google.inject.Inject;
import io.github.flibio.economylite.EconomyLite;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin( id=AtherysTowns.ID, name="A'therys Towns", description = "A custom plugin responsible for agile land management. Created for the A'therys Horizons server.", version="1.0")
public class AtherysTowns {

    final static String ID = "atherystowns";

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    private static AtherysTowns instance;

    private Map<Class<? extends TownsObject>,DatabaseManager<? extends TownsObject>> managers = new HashMap<>();
    private ResidentManager residentManager;
    private PlotManager plotManager;
    private TownManager townManager;
    private NationManager nationManager;

    private TownsDatabase database;

    private Task townBorderTask;
    private Task wildernessRegenTask;

    public Task getTownBorderTask() {
        return townBorderTask;
    }

    private boolean init() {
        instance = this;

        try {
            Settings.load();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Settings file could not load. Halting AtherysTowns & Server initialization.");
            game.getServer().shutdown();
            return false;
        }

        database = TownsDatabase.getInstance();
        if ( database == null ) return false;

        nationManager = new NationManager();
        nationManager.setup();
        managers.put( Nation.class, nationManager);

        townManager = new TownManager();
        townManager.setup();
        managers.put(Town.class, townManager);

        plotManager = new PlotManager();
        plotManager.setup();
        managers.put(Plot.class, plotManager);

        residentManager = new ResidentManager();
        residentManager.setup();
        managers.put(Resident.class, residentManager);

        WildernessManager.setup();

        // TODO: Loading mechanism
        // TODO: Configs

        return true;
    }

    private void start() {
        game.getEventManager().registerListeners(this, new PlayerListeners());

        ResidentCommand.register();
        PlotMasterCommand.getInstance().register();
        TownMasterCommand.getInstance().register();
        NationMasterCommand.getInstance().register();

        townBorderTask = Task.builder()
                .interval(Settings.TOWN_BORDER_UPDATE_SECONDS, TimeUnit.SECONDS)
                .execute(() -> {
                    for ( Player p : Sponge.getServer().getOnlinePlayers() ) {
                        if (TownsValues.get(p.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS).isPresent()){
                            Optional<Resident> resOpt = AtherysTowns.getInstance().getResidentManager().get(p.getUniqueId());
                            if ( resOpt.isPresent() ) {
                                if ( resOpt.get().town().isPresent() ) {
                                    resOpt.get().town().get().showBorders(p);
                                }
                            }
                        }
                    }
                })
                .name("atherystowns-town-border-task")
                .submit(this);

        wildernessRegenTask = Task.builder()
                .delay(Settings.WILDERNESS_REGEN_RATE, Settings.WILDERNESS_REGEN_RATE_UNIT)
                .interval(Settings.WILDERNESS_REGEN_RATE, TimeUnit.SECONDS)
                .execute(WildernessManager::task)
                .name("atherystowns-wilderness-regen-task")
                .submit(this);
    }

    private void stop() {
        database.saveAll();
        /*try {
            Settings.save();
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Settings file could not save.");
        }*/
    }

   /* @Listener
    public void onInit (GameInitializationEvent event) {
        init();
    }*/

    @Listener
    public void onStart (GameStartedServerEvent event) {
        if ( init() ) start();
    }

    @Listener
    public void onServerStopped (GameStoppingServerEvent event ) {
        stop();
    }

    public static AtherysTowns getInstance() { return instance; }

    public ResidentManager getResidentManager() { return residentManager; }
    public TownManager getTownManager() { return townManager; }
    public PlotManager getPlotManager() { return plotManager; }
    public NationManager getNationManager() { return nationManager; }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Optional<EconomyLite> getEconomyPlugin() {
        Optional<PluginContainer> econOpt = game.getPluginManager().getPlugin("economylite");
        if ( econOpt.isPresent() ) {
            return Optional.of(EconomyLite.getInstance());
        } else return Optional.empty();
    }

    public TownsDatabase getDatabase() {
        return database;
    }

    public Collection<DatabaseManager<? extends TownsObject>> getDbManagers() {
        return managers.values();
    }
}
