package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class NationService {

    public static final Text DEFAULT_NATION_DESCRIPTION = Text.of("No description available.");

    private TownService townService;

    private PermissionService permissionService;

    private ResidentService residentService;

    private ResidentRepository residentRepository;

    private TownRepository townRepository;

    private TownsConfig config;

    private Map<String, Nation> nations;

    @Inject
    NationService(
            TownService townService,
            TownRepository townRepository,
            TownsConfig config,
            PermissionService permissionService,
            ResidentService residentService,
            ResidentRepository residentRepository) {
        this.townService = townService;
        this.townRepository = townRepository;
        this.config = config;
        this.permissionService = permissionService;
        this.residentService = residentService;
        this.residentRepository = residentRepository;

        ImmutableMap.Builder<String, Nation> nationBuilder = ImmutableMap.builder();

        for (NationConfig nationConfig : config.NATIONS) {
            Resident leader = residentRepository.findById(nationConfig.getLeaderUuid()).orElse(null);
            Town capital = townRepository.findById(nationConfig.getCapitalId()).orElse(null);

            Account account = null;
            if (AtherysTowns.economyIsEnabled()) {
                account = Economy.getAccount(nationConfig.getBank().toString()).orElse(null);
            }

            Nation newNation = new Nation(
                nationConfig.getId(),
                nationConfig.getName(),
                nationConfig.getDescription(),
                leader,
                capital,
                nationConfig.isFreelyJoinable(),
                nationConfig.getTax(),
                account,
                nationConfig.getNationLeaderRole(),
                nationConfig.getNationDefaultRole(),
                nationConfig.getRoles().stream().map(NationRoleConfig::getId).collect(Collectors.toSet())
            );

            nationBuilder.put(nationConfig.getId(), newNation);
        }
    }

    public Optional<Nation> getNationFromId(String nationName) {
        return Optional.ofNullable(nations.get(nationName));
    }

    public Map<String, Nation> getNations() {
        return nations;
    }

    public Set<Town> getTownsInNation(Nation nation) {
        return townRepository.getAll().stream()
                .filter(town -> nation.equals(town.getNation()))
                .collect(Collectors.toSet());
    }

    public int getNationPopulation(Nation nation) {
        return townRepository.getAll().stream()
                .filter(town -> town.getNation() != null && town.getNation().equals(nation.getId()))
                .mapToInt(town -> town.getResidents().size())
                .reduce(0, Integer::sum);
    }
}
