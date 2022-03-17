package com.atherys.towns;

import com.atherys.core.AtherysCore;
import com.atherys.core.command.CommandService;
import com.atherys.core.economy.Economy;
import com.atherys.core.event.AtherysDatabaseMigrationEvent;
import com.atherys.core.event.AtherysHibernateConfigurationEvent;
import com.atherys.core.event.AtherysHibernateInitializedEvent;
import com.atherys.towns.api.permission.*;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.command.nation.NationCommand;
import com.atherys.towns.command.plot.PlotCommand;
import com.atherys.towns.command.rent.RentCommand;
import com.atherys.towns.command.resident.ResidentCommand;
import com.atherys.towns.command.town.TownCommand;
import com.atherys.towns.facade.*;
import com.atherys.towns.integration.AtherysChatIntegration;
import com.atherys.towns.listener.PlayerListener;
import com.atherys.towns.listener.ProtectionListener;
import com.atherys.towns.listener.RaidListener;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.permission.TownsContextCalculator;
import com.atherys.towns.persistence.*;
import com.atherys.towns.persistence.cache.TownPlotCache;
import com.atherys.towns.persistence.cache.TownsCache;
import com.atherys.towns.service.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import static com.atherys.towns.AtherysTowns.*;

@Plugin(
        id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "atherysparties", optional = true),
                @Dependency(id = "atheryschat", optional = true)
        }
)
public class AtherysTowns {

    final static String ID = "atherystowns";
    final static String NAME = "A'therys Towns";
    final static String DESCRIPTION = "A land-management plugin for the Atherys Horizons server";
    final static String VERSION = "%PROJECT_VERSION%";

    private static AtherysTowns instance;
    private static boolean init = false;
    private boolean economyEnabled;
    @Inject
    private Logger logger;

    @Inject
    private Injector spongeInjector;

    private Components components;

    private Injector townsInjector;

    public static AtherysTowns getInstance() {
        return instance;
    }

    public static boolean economyIsEnabled() {
        return getInstance().economyEnabled;
    }

    private void init() {
        instance = this;

        // Register Permission Catalogue registry module
        Sponge.getRegistry().registerModule(Permission.class, new PermissionRegistryModule());
        Sponge.getRegistry().registerModule(WorldPermission.class, new WorldPermissionRegistryModule());
        Sponge.getRegistry().registerModule(TownsPermissionContext.class, new TownsPermissionContextRegistryModule());

        components = new Components();
        townsInjector = spongeInjector.createChildInjector(new AtherysTownsModule());
        townsInjector.injectMembers(components);

        init = true;
    }

    private void start() {
        getConfig().init();
        economyEnabled = Economy.isPresent() && components.config.ECONOMY;

        if (components.config.TOWN.TOWN_WARMUP < 0) {
            logger.warn("Town spawn warmup is negative. Will default to zero.");
            components.config.TOWN.TOWN_WARMUP = 0;
        }

        if (components.config.TOWN.TOWN_COOLDOWN < 0) {
            components.config.TOWN.TOWN_COOLDOWN = 0;
            logger.warn("Town spawn cooldown is negative. Will default to zero.");
        }

        getRoleService().init();
        getTownsCache().initCache();
        getTownRaidService().initRaidTimer();
        getPlotBorderFacade().initBorderTask();

        if (economyIsEnabled()) {
            getTaxFacade().init();
            getRentService().init();
        }

        Sponge.getEventManager().registerListeners(this, components.playerListener);
        Sponge.getEventManager().registerListeners(this, components.protectionListener);
        Sponge.getEventManager().registerListeners(this, components.raidListener);

        if (Sponge.getPluginManager().isLoaded("atheryschat")) {
            AtherysChatIntegration.registerChannels();
        }

        Sponge.getServiceManager()
                .provideUnchecked(org.spongepowered.api.service.permission.PermissionService.class)
                .registerContextCalculator(new TownsContextCalculator());

        try {
            AtherysCore.getCommandService().register(new ResidentCommand(), this);
            AtherysCore.getCommandService().register(new PlotCommand(), this);
            AtherysCore.getCommandService().register(new TownCommand(), this);
            AtherysCore.getCommandService().register(new NationCommand(), this);

            if (economyIsEnabled()) {
                AtherysCore.getCommandService().register(new RentCommand(), this);
            }
        } catch (CommandService.AnnotatedCommandException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        getTownsCache().flushCache();
    }

    @Listener
    public void onHibernateInit(AtherysHibernateInitializedEvent event) {
        init();
    }

    @Listener
    public void onHibernateConfiguration(AtherysHibernateConfigurationEvent event) {
        event.registerEntity(Nation.class);
        event.registerEntity(Town.class);
        event.registerEntity(NationPlot.class);
        event.registerEntity(RentInfo.class);
        event.registerEntity(TownPlot.class);
        event.registerEntity(Resident.class);
        event.registerEntity(TownPlotPermission.class);
    }

    @Listener
    public void onDatabaseMigration(AtherysDatabaseMigrationEvent event) {
        event.registerForMigration(ID);
    }

    @Listener(order = Order.LAST)
    public void onStart(GameStartingServerEvent event) {
        if (init) start();
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        if (init) stop();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        getConfig().init();
    }

    public TownsConfig getConfig() {
        return components.config;
    }

    public Logger getLogger() {
        return logger;
    }

    public TownRepository getTownRepository() {
        return components.townRepository;
    }

    public NationRepository getNationRepository() {
        return components.nationRepository;
    }

    public TownPlotRepository getTownPlotRepository() {
        return components.townPlotRepository;
    }

    public NationPlotRepository getNationPlotRepository() {
        return components.nationPlotRepository;
    }

    public ResidentRepository getResidentRepository() {
        return components.residentRepository;
    }

    public TaxService getTaxService() {
        return components.taxService;
    }

    public PollService getPollService() {
        return components.pollService;
    }

    public NationService getNationService() {
        return components.nationService;
    }

    public TownService getTownService() {
        return components.townService;
    }

    public PlotService getPlotService() {
        return components.plotService;
    }

    public ResidentService getResidentService() {
        return components.residentService;
    }

    public RoleService getRoleService() {
        return components.roleService;
    }

    public RentService getRentService() {
        return components.rentService;
    }

    public TownsPermissionService getPermissionService() {
        return components.townsPermissionService;
    }

    public TownsMessagingFacade getTownsMessagingService() {
        return components.townsMessagingFacade;
    }

    public TownRaidService getTownRaidService() {
        return components.townRaidService;
    }

    public NationFacade getNationFacade() {
        return components.nationFacade;
    }

    public TownFacade getTownFacade() {
        return components.townFacade;
    }

    public TownSpawnFacade getTownSpawnCommand() {
        return components.townSpawnFacade;
    }

    public TownAdminFacade getTownAdminFacade() {
        return components.townAdminFacade;
    }

    public PlotFacade getPlotFacade() {
        return components.plotFacade;
    }

    public ResidentFacade getResidentFacade() {
        return components.residentFacade;
    }

    public PermissionFacade getPermissionFacade() {
        return components.permissionFacade;
    }

    public PlotSelectionFacade getPlotSelectionFacade() {
        return components.plotSelectionFacade;
    }

    public PollFacade getPollFacade() {
        return components.pollFacade;
    }

    public PlotBorderFacade getPlotBorderFacade() {
        return components.plotBorderFacade;
    }

    public TownRaidFacade getTownRaidFacade() {
        return components.townRaidFacade;
    }

    public TaxFacade getTaxFacade() {
        return components.taxFacade;
    }

    public RentFacade getRentFacade() {
        return components.rentFacade;
    }

    public TownsCache getTownsCache() {
        return components.townsCache;
    }

    private static class Components {

        @Inject
        private TownsConfig config;

        @Inject
        private TownsCache townsCache;

        @Inject
        private TownPlotCache townPlotCache;

        @Inject
        private NationRepository nationRepository;

        @Inject
        private TownRepository townRepository;

        @Inject
        private TownPlotRepository townPlotRepository;

        @Inject
        private RentInfoRepository rentInfoRepository;

        @Inject
        private NationPlotRepository nationPlotRepository;

        @Inject
        private ResidentRepository residentRepository;

        @Inject
        private TaxService taxService;

        @Inject
        private PollService pollService;

        @Inject
        private NationService nationService;

        @Inject
        private TownService townService;

        @Inject
        private PlotService plotService;

        @Inject
        private ResidentService residentService;

        @Inject
        private RoleService roleService;

        @Inject
        private TownsPermissionService townsPermissionService;

        @Inject
        private TownRaidService townRaidService;

        @Inject
        private RentService rentService;

        @Inject
        private TownsMessagingFacade townsMessagingFacade;

        @Inject
        private NationFacade nationFacade;

        @Inject
        private TownFacade townFacade;

        @Inject
        private TownSpawnFacade townSpawnFacade;

        @Inject
        private TownAdminFacade townAdminFacade;

        @Inject
        private PlotFacade plotFacade;

        @Inject
        private ResidentFacade residentFacade;

        @Inject
        private PermissionFacade permissionFacade;

        @Inject
        private PlotSelectionFacade plotSelectionFacade;

        @Inject
        private PollFacade pollFacade;

        @Inject
        private PlotBorderFacade plotBorderFacade;

        @Inject
        private TownRaidFacade townRaidFacade;

        @Inject
        private TaxFacade taxFacade;

        @Inject
        private RentFacade rentFacade;

        @Inject
        private PlayerListener playerListener;

        @Inject
        private ProtectionListener protectionListener;

        @Inject
        private RaidListener raidListener;
    }
}
