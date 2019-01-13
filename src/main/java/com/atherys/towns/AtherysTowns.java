package com.atherys.towns;

import com.atherys.core.event.AtherysHibernateConfigurationEvent;
import com.atherys.core.event.AtherysHibernateInitializedEvent;
import com.atherys.towns.model.*;
import com.atherys.towns.persistence.*;
import com.atherys.towns.service.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
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
    final static String VERSION = "1.0.0";

    private static AtherysTowns instance;

    private static boolean init = false;

    @Inject
    private Logger logger;

    @Inject
    private Injector spongeInjector;

    private TownsConfig config;

    private NationRepository nationRepository;

    private TownRepository townRepository;

    private PlotRepository plotRepository;

    private ResidentRepository residentRepository;

    private PermissionRepository permissionRepository;

    private NationService nationService;

    private TownService townService;

    private PlotService plotService;

    private ResidentService residentService;

    private PermissionService permissionService;

    private Injector townsInjector;

    private void init() {
        instance = this;


        townsInjector = spongeInjector.createChildInjector(new AtherysTownsModule());

        config = getInstance(TownsConfig.class);

        nationRepository = getInstance(NationRepository.class);
        townRepository = getInstance(TownRepository.class);
        plotRepository = getInstance(PlotRepository.class);
        residentRepository = getInstance(ResidentRepository.class);
        permissionRepository = getInstance(PermissionRepository.class);

        nationService = getInstance(NationService.class);
        townService = getInstance(TownService.class);
        plotService = getInstance(PlotService.class);
        residentService = getInstance(ResidentService.class);
        permissionService = getInstance(PermissionService.class);

        config.init();
    }

    private void start() {
    }

    private void stop() {

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
        event.registerEntity(PermissionNode.class);
    }

    @Listener
    public void onStart(GameStartingServerEvent event) {
        if (init) start();
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        if (init) stop();
    }

    public static AtherysTowns getInstance() {
        return instance;
    }

    public TownsConfig getConfig() {
        return config;
    }

    public NationRepository getNationRepository() {
        return nationRepository;
    }

    public TownRepository getTownRepository() {
        return townRepository;
    }

    public PlotRepository getPlotRepository() {
        return plotRepository;
    }

    public ResidentRepository getResidentRepository() {
        return residentRepository;
    }

    public PermissionRepository getPermissionRepository() {
        return permissionRepository;
    }

    public NationService getNationService() {
        return nationService;
    }

    public TownService getTownService() {
        return townService;
    }

    public PlotService getPlotService() {
        return plotService;
    }

    public ResidentService getResidentService() {
        return residentService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public <T> T getInstance(Class<T> clazz) {
        return townsInjector.getInstance(clazz);
    }
}
