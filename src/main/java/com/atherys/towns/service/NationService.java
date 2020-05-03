package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.NationRepository;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class NationService {

    public static final Text DEFAULT_NATION_DESCRIPTION = Text.of("No description available.");

    private NationRepository nationRepository;

    private TownService townService;

    private PermissionService permissionService;

    private ResidentService residentService;

    private TownRepository townRepository;

    private TownsConfig config;

    @Inject
    NationService(
            NationRepository nationRepository,
            TownService townService,
            TownRepository townRepository,
            TownsConfig config,
            PermissionService permissionService,
            ResidentService residentService) {
        this.nationRepository = nationRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.config = config;
        this.permissionService = permissionService;
        this.residentService = residentService;
    }

    public Optional<NationConfig> getNationFromName(String nationName) {
        return nationRepository.findByName(nationName);
    }

    public NationConfig createNation(String name, Town capital) {
        NationConfig nation = new NationConfig();
        nation.setName(name);
        nation.setDescription(DEFAULT_NATION_DESCRIPTION);

        nationRepository.saveOne(nation);

        townService.setTownNation(capital, nation);
        nation.setCapital(capital);
        nation.setLeader(capital.getLeader());

        NationRole leaderRole = permissionService.getNationLeaderRole();
        NationRole memberRole = permissionService.getNationDefaultRole();

        residentService.grantRole(nation.getLeader(), leaderRole);

        // TODO: This will update every resident of the town separately, which is inefficient
        capital.getResidents().forEach(resident -> residentService.grantRole(resident, memberRole));

        // permissionService.permit(nation.getLeader(), nation, config.DEFAULT_NATION_LEADER_PERMISSIONS);
        // permissionService.permit(capital, nation, config.DEFAULT_NATION_TOWN_PERMISSIONS);

        nation.setBank(UUID.randomUUID());
        if (AtherysTowns.economyIsEnabled()) {
            AtherysCore.getEconomyService().get().getOrCreateAccount(nation.getBank().toString());
        }

        nationRepository.saveOne(nation);

        return nation;
    }

    public void setNationName(NationConfig nation, String name) {
        nation.setName(name);
        nationRepository.saveOne(nation);
    }

    public void setNationDescription(NationConfig nation, Text description) {
        nation.setDescription(description);
        nationRepository.saveOne(nation);
    }

    public void addTown(NationConfig nation, Town town) {
        town.setNation(nation);
        nation.addTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
    }

    public void removeTown(NationConfig nation, Town town) {
        if (town.getNation() != null) {
            town.getResidents().forEach(resident -> permissionService.removeAll(resident, nation));
        }
        town.setNation(null);
        nation.removeTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
    }

    public int getNationPopulation(NationConfig nation) {
        return nation.getTowns().stream()
                .mapToInt(town -> town.getResidents().size())
                .reduce(0, Integer::sum);
    }

    public void setCapital(NationConfig nation, Town town) {
        nation.setCapital(town);
        nationRepository.saveOne(nation);
    }

    public void addNationAlly(NationConfig nation, NationConfig ally) {
        nation.addAlly(ally);
        nationRepository.saveOne(nation);
    }

    public void addNationNeutral(NationConfig nation, NationConfig neutral) {
        nation.removeAlly(neutral);
        nation.removeEnemy(neutral);

        nationRepository.saveOne(nation);
    }

    public void addNationEnemy(NationConfig nation, NationConfig enemy) {
        nation.addEnemy(enemy);
        nationRepository.saveOne(nation);
    }

    public Collection<NationConfig> getAllNations() {
        return nationRepository.getAllNations();
    }

}
