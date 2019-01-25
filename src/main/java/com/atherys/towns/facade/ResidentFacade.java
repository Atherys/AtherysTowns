package com.atherys.towns.facade;

import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
public class ResidentFacade {

    @Inject
    ResidentService residentService;

    @Inject
    TownService townService;

    ResidentFacade() {}

    public boolean isPlayerInTown(Player player, Town town) {
        return town.equals(residentService.getOrCreate(player).getTown());
    }

    public boolean isPlayerInNation(Player player, Nation nation) {
        Resident resident = residentService.getOrCreate(player);

        if ( resident.getTown() == null ) {
            return false;
        }

        return nation.equals(resident.getTown().getNation());
    }
}
