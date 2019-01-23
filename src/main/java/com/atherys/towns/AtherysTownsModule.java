package com.atherys.towns;

import com.atherys.towns.api.chat.TownsChatService;
import com.atherys.towns.facade.*;
import com.atherys.towns.persistence.*;
import com.atherys.towns.service.*;
import com.atherys.towns.service.chat.SimpleTownsChatService;
import com.google.inject.AbstractModule;

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
        bind(TownsChatService.class).to(SimpleTownsChatService.class);

        // Facades
        bind(NationFacade.class);
        bind(TownFacade.class);
        bind(PlotFacade.class);
        bind(ResidentFacade.class);
        bind(PermissionFacade.class);
        bind(PlotSelectionFacade.class);
        bind(TownsMessagingFacade.class);
    }
}
