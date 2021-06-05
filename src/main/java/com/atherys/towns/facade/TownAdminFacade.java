package com.atherys.towns.facade;

import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandSource;

@Singleton
public class TownAdminFacade {
    @Inject
    TownService townService;

    @Inject
    TownsMessagingFacade townsMsg;

    public void decreaseTownSize(CommandSource source, Town town, int amount) {
        if (town.getMaxSize() - amount > townService.getTownSize(town)) {
            townService.decreaseTownSize(town, amount);
            townsMsg.info(source, "Town size decreased.");
        } else {
            townsMsg.error(source, "Town size could not be decreased. You are trying to decrease by too much.");
        }
    }

    public void increaseTownSize(CommandSource source, Town town, int amount) {
        townService.increaseTownSize(town, amount);
        townsMsg.info(source, "Town size increased.");
    }
}
