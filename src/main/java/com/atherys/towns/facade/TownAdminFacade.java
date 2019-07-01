package com.atherys.towns.facade;

import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

public class TownAdminFacade {
    @Inject
    TownService townService;

    @Inject
    TownsMessagingFacade townsMsg;

    public void decreaseTownSize(CommandSource source, String townName, int amount) throws TownsCommandException {
        Town town = townService.getTownFromName(Text.of(townName)).orElseThrow(TownsCommandException::townNotFound);

        if (town.getMaxSize() - amount > townService.getTownSize(town)) {
            townService.decreaseTownSize(town, amount);
            townsMsg.info(source, "Town size decreased.");
        } else {
            townsMsg.error(source, "Town size could not be decreased. You are trying to decrease by too much.");
        }
    }

    public void increaseTownSize(CommandSource source, String townName, int amount) throws TownsCommandException {
        Town town = townService.getTownFromName(Text.of(townName)).orElseThrow(TownsCommandException::townNotFound);

        townService.increaseTownSize(town, amount);
        townsMsg.info(source, "Town size increased.");
    }
}
