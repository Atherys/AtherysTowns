package com.atherys.towns.service;

import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Singleton;

@Singleton
public class TownService {

    private TownRepository townRepository;

    TownService(
            TownRepository townRepository
    ) {
        this.townRepository = townRepository;
    }

    public void setTownNation(Town town, Nation nation) {

        // if town is already part of another nation, remove it
        if ( town.getNation() != null ) {
            town.getNation().removeTown(town);
        }

        town.setNation(nation);
        nation.addTown(town);

        townRepository.saveOne(town);
    }
}
