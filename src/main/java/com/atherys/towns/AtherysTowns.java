package com.atherys.towns;

import com.atherys.chat.AtherysChat;
import com.atherys.core.AtherysCore;
import com.atherys.core.command.CommandService;
import com.atherys.core.economy.Economy;
import com.atherys.core.event.AtherysHibernateConfigurationEvent;
import com.atherys.core.event.AtherysHibernateInitializedEvent;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.PermissionRegistryModule;
import com.atherys.towns.api.permission.WorldPermissionRegistryModule;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.chat.NationChannel;
import com.atherys.towns.chat.TownChannel;
import com.atherys.towns.command.nation.NationCommand;
import com.atherys.towns.command.plot.PlotCommand;
import com.atherys.towns.command.resident.ResidentCommand;
import com.atherys.towns.command.town.TownCommand;
import com.atherys.towns.facade.*;
import com.atherys.towns.listener.PlayerListener;
import com.atherys.towns.listener.ProtectionListener;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.permission.TownsContextCalculator;
import com.atherys.towns.persistence.NationRepository;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.atherys.towns.persistence.cache.TownsCache;
import com.atherys.towns.service.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
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
                @Dependency(id = "atheryscore")
        }
)
public class AtherysTowns {

    final static String ID = "atherystowns";
    final static String NAME = "A'therys Towns";
    final static String DESCRIPTION = "A land-management plugin for the Atherys Horizons server";
    final static String VERSION = "%PLUGIN_VERSION%";

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

        components = new Components();
        townsInjector = spongeInjector.createChildInjector(new AtherysTownsModule());
        townsInjector.injectMembers(components);

        getConfig().init();

        init = true;
    }

    private void start() {
        getRoleService().init();
        getTownsCache().initCache();
        getPlotBorderFacade().initBorderTask();

        Sponge.getEventManager().registerListeners(this, components.playerListener);
        Sponge.getEventManager().registerListeners(this, components.protectionListener);
        AtherysChat.getInstance().getChatService().registerChannel(new TownChannel());
        AtherysChat.getInstance().getChatService().registerChannel(new NationChannel());

        economyEnabled = Economy.isPresent() && components.config.ECONOMY;

        Sponge.getServiceManager()
                .provideUnchecked(org.spongepowered.api.service.permission.PermissionService.class)
                .registerContextCalculator(new TownsContextCalculator());

        try {
            AtherysCore.getCommandService().register(new ResidentCommand(), this);
            AtherysCore.getCommandService().register(new PlotCommand(), this);
            AtherysCore.getCommandService().register(new TownCommand(), this);
            AtherysCore.getCommandService().register(new NationCommand(), this);
        } catch (CommandService.AnnotatedCommandException e) {
            e.printStackTrace();
        }

        if (components.config.TOWN.TOWN_WARMUP < 0) {
            logger.warn("Town spawn warmup is negative. Will default to zero.");
            components.config.TOWN.TOWN_WARMUP = 0;
        }

        if (components.config.TOWN.TOWN_COOLDOWN < 0) {
            components.config.TOWN.TOWN_COOLDOWN = 0;
            logger.warn("Town spawn cooldown is negative. Will default to zero.");
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
        event.registerEntity(Plot.class);
        event.registerEntity(Resident.class);
    }

    @Listener
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

    public PlotRepository getPlotRepository() {
        return components.plotRepository;
    }

    public ResidentRepository getResidentRepository() {
        return components.residentRepository;
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

    public TownsPermissionService getPermissionService() {
        return components.townsPermissionService;
    }

    public TownsMessagingFacade getTownsMessagingService() {
        return components.townsMessagingFacade;
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

    public ProtectionFacade getProtectionFacade() {
        return components.protectionFacade;
    }

    public PlotBorderFacade getPlotBorderFacade() {
        return components.plotBorderFacade;
    }

    public TownsCache getTownsCache() {
        return components.townsCache;
    }


    private static class Components {

        @Inject
        PlotBorderFacade plotBorderFacade;
        @Inject
        private TownsConfig config;
        @Inject
        private TownsCache townsCache;
        @Inject
        private NationRepository nationRepository;

        @Inject
        private TownRepository townRepository;
        @Inject
        private PlotRepository plotRepository;
        @Inject
        private ResidentRepository residentRepository;
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
        private ProtectionFacade protectionFacade;
        @Inject
        private PlayerListener playerListener;
        @Inject
        private ProtectionListener protectionListener;
    }
}
