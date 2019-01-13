package com.atherys.towns;

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
    }
}
