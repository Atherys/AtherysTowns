package com.atherys.towns;

import com.atherys.towns.facade.NationFacade;
import com.atherys.towns.facade.PermissionFacade;
import com.atherys.towns.facade.PlotFacade;
import com.atherys.towns.facade.ResidentFacade;
import com.atherys.towns.facade.TownFacade;
import com.atherys.towns.persistence.*;
import com.atherys.towns.service.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AtherysTownsModule extends AbstractModule {
    @Override
    protected void configure() {
        // Config
        bind(TownsConfig.class);

        // Repositories
        bind(NationRepository.class);
        bind(TownRepository.class);
        bind(PlotRepository.class);
        bind(ResidentRepository.class);
        bind(PermissionRepository.class);

        // Services
        bind(NationService.class);
        bind(TownService.class);
        bind(PlotService.class);
        bind(ResidentService.class);
        bind(PermissionService.class);

        // Facades
        bind(NationFacade.class);
        bind(TownFacade.class);
        bind(PlotFacade.class);
        bind(ResidentFacade.class);
        bind(PermissionFacade.class);
    }
}
