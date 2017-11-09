package com.atherys.towns;

import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.commands.nation.NationMasterCommand;
import com.atherys.towns.commands.plot.PlotMasterCommand;
import com.atherys.towns.commands.resident.ResidentCommand;
import com.atherys.towns.commands.town.TownMasterCommand;
import com.atherys.towns.commands.wilderness.WildernessRegenCommand;
import com.atherys.towns.db.TownsDatabase;
import com.atherys.towns.listeners.PlayerListeners;
import com.atherys.towns.managers.*;
import com.atherys.towns.resident.Resident;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.permission.PermissionService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.atherys.towns.AtherysTowns.*;

@Plugin( id=AtherysTowns.ID, name=NAME, description = DESCRIPTION, version=VERSION)
public class AtherysTowns {

    final static String ID = "atherystowns";
    final static String NAME = "A'therys Towns";
    final static String DESCRIPTION = "A custom plugin responsible for agile land management. Created for the A'therys Horizons server.";
    final static String VERSION = "1.0.0a";

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    private static AtherysTowns instance;

    private Task townBorderTask;
    private Task wildernessRegenTask;

    private boolean init() {
        instance = this;

        if ( !getPermissionService().isPresent() ) {
            getLogger().warn("No permission service found. This plugin requires a permissions plugin utilizing the Sponge Permissions API to function properly. Aborting start.");
            return false;
        }

        if ( !getEconomyService().isPresent() ) {
            getLogger().warn("No economy service found. No features relating to economy will function!");
        }

        Settings.getInstance();

        TownsDatabase.getInstance();

        WildernessManager.getInstance();

        NationManager.getInstance().loadAll();

        TownManager.getInstance().loadAll();

        PlotManager.getInstance().loadAll();

        ResidentManager.getInstance().loadAll();

        return true;
    }

    private void start() {
        game.getEventManager().registerListeners(this, new PlayerListeners());

        ResidentCommand.register();
        Sponge.getCommandManager().register(AtherysTowns.getInstance(), PlotMasterCommand.getInstance().getSpec(), "plot", "p");
        Sponge.getCommandManager().register(AtherysTowns.getInstance(), TownMasterCommand.getInstance().getSpec(), "town", "t");
        Sponge.getCommandManager().register(AtherysTowns.getInstance(), NationMasterCommand.getInstance().getSpec(), "nation", "n");
        WildernessRegenCommand.getInstance().register();

        townBorderTask = Task.builder()
                .interval( Settings.TOWN_BORDER_UPDATE_RATE, TimeUnit.SECONDS )
                .execute(() -> {
                    for ( Player p : Sponge.getServer().getOnlinePlayers() ) {
                        if (TownsValues.get(p.getUniqueId(), TownsValues.TownsKey.TOWN_BORDERS).isPresent()){
                            Optional<Resident> resOpt = ResidentManager.getInstance().get(p.getUniqueId());
                            if ( resOpt.isPresent() ) {
                                if ( resOpt.get().getTown().isPresent() ) {
                                    resOpt.get().getTown().get().showBorders(p);
                                }
                            }
                        }
                    }
                })
                .name("atherystowns-town-border-task")
                .submit(this);

        if ( Settings.WILDERNESS_REGEN_ENABLED ) {
            wildernessRegenTask = Task.builder()
                    //.delay(  ) TODO: When launching, delay this regen task to ensure it is run every WILDERNESS_REGEN_RATE units of time.
                    .interval(Settings.WILDERNESS_REGEN_RATE, Settings.WILDERNESS_REGEN_RATE_UNIT)
                    .execute(() -> WildernessManager.getInstance().regenerate(System.currentTimeMillis()))
                    .name("atherystowns-wilderness-regen-task")
                    .submit(this);
        }
    }

    private void stop() {
        ResidentManager.getInstance().saveAll();
        PlotManager.getInstance().saveAll();
        TownManager.getInstance().saveAll();
        NationManager.getInstance().saveAll();
    }

    @Listener
    public void onStart (GameStartedServerEvent event) {
        if ( init() ) start();
    }

    @Listener
    public void onStop (GameStoppingServerEvent event ) {
        stop();
    }

    public static AtherysTowns getInstance() { return instance; }

    public static MongoDatabase getDb() {
        return TownsDatabase.getInstance().getDatabase();
    }

    public Game getGame() {
        return game;
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
}
