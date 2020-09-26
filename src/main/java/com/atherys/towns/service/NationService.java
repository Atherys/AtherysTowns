package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.NationPlot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.NationPlotRepository;
import com.atherys.towns.persistence.NationRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class NationService {

    public static final Text DEFAULT_NATION_DESCRIPTION = Text.of("No description available.");

    @Inject
    private TownService townService;

    @Inject
    private TownsPermissionService townsPermissionService;

    @Inject
    private ResidentService residentService;

    @Inject
    private RoleService roleService;

    @Inject
    private ResidentRepository residentRepository;

    @Inject
    private TownRepository townRepository;

    @Inject
    private NationRepository nationRepository;

    @Inject
    private NationPlotRepository nationPlotRepository;

    @Inject
    private TownsConfig config;

    public Optional<Nation> getNationFromName(String nationName) {
        return nationRepository.findByName(nationName);
    }

    public Nation createNation(String name, Town capital) {
        Nation nation = new Nation();

        nation.setName(name);
        nation.setDescription(DEFAULT_NATION_DESCRIPTION);

        nationRepository.saveOne(nation);

        townService.setTownNation(capital, nation);
        nation.addTown(capital);
        nation.setCapital(capital);
        nation.setLeader(capital.getLeader());

        residentService.getUserFromResident(capital.getLeader()).ifPresent(user -> {
            roleService.addNationRole(user, nation, config.NATION.LEADER_ROLE);
            roleService.addNationRole(user, nation, config.NATION.DEFAULT_ROLE);
        });

        nation.setBank(UUID.randomUUID());
        if (AtherysTowns.economyIsEnabled()) {
            AtherysCore.getEconomyService().get().getOrCreateAccount(nation.getBank());
        }

        nationRepository.saveOne(nation);

        return nation;
    }

    public void disbandNation(Nation nation) {
        for (Town town : nation.getTowns()) {
            removeTown(nation, town);
        }

        nationRepository.deleteOne(nation);
    }

    public int getNationPopulation(Nation nation) {
        return nation.getTowns().stream().mapToInt(town -> town.getResidents().size()).sum();
    }

    public void setNationName(Nation nation, String name) {
        nation.setName(name);
        nationRepository.saveOne(nation);
    }

    public void setNationDescription(Nation nation, Text description) {
        nation.setDescription(description);
        nationRepository.saveOne(nation);
    }

    public void addTown(Nation nation, Town town) {
        town.setNation(nation);
        nation.addTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
    }

    public void removeTown(Nation nation, Town town) {
        // Remove permissions from the residents of the town been removed
        for (Resident resident : town.getResidents()) {
            residentService.getUserFromResident(resident).ifPresent(user -> {
                townsPermissionService.clearPermissions(user, nation);
            });
        }

        town.setNation(null);
        nation.removeTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
    }

    public void setCapital(Nation nation, Town town) {
        nation.setCapital(town);
        nationRepository.saveOne(nation);
    }

    public void setTax(Nation nation, double tax) {
        nation.setTax(tax);
        nationRepository.saveOne(nation);
    }

    public void addNationAlly(Nation nation, Nation ally) {
        nation.removeEnemy(ally);
        nation.addAlly(ally);
        nationRepository.saveOne(nation);
    }

    public void addNationNeutral(Nation nation, Nation neutral) {
        nation.removeAlly(neutral);
        nation.removeEnemy(neutral);

        nationRepository.saveOne(nation);
    }

    public void addNationEnemy(Nation nation, Nation enemy) {
        nation.removeAlly(enemy);
        nation.addEnemy(enemy);
        nationRepository.saveOne(nation);
    }

    public void claimPlotForNation(NationPlot plot, Nation nation) {
        nation.addPlot(plot);
        plot.setNation(nation);

        nationPlotRepository.saveOne(plot);
        nationRepository.saveOne(nation);
    }

    public void removeNationPlot(NationPlot plot) {
        Nation nation = plot.getNation();
        nation.removePlot(plot);

        nationRepository.saveOne(nation);
        nationPlotRepository.deleteOne(plot);
    }

    public Collection<Nation> getAllNations() {
        return nationRepository.getAll();
    }
}
