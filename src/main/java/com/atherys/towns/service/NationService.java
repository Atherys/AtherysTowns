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
import com.flowpowered.noise.module.modifier.ScalePoint;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

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
    private ResidentRepository residentRepository;

    @Inject
    private TownRepository townRepository;

    @Inject
    private TownsConfig config;

    private Map<String, Nation> nations;

    public void init() {
        ImmutableMap.Builder<String, Nation> nationBuilder = ImmutableMap.builder();

        for (NationConfig nationConfig : config.NATIONS) {


            Account account = null;
            if (AtherysTowns.economyIsEnabled()) {
                account = Economy.getAccount(nationConfig.getBank().toString()).orElse(null);
            }

            Nation newNation = new Nation(
                nationConfig.getId(),
                nationConfig.getName(),
                nationConfig.getDescription(),
                null,
                null,
                nationConfig.isFreelyJoinable(),
                nationConfig.getTax(),
                account
            );

            nationBuilder.put(nationConfig.getId(), newNation);
        }
        this.nations = nationBuilder.build();
    }

    public void initTowns() {
        for (NationConfig nationConfig : config.NATIONS) {
            Resident leader = residentRepository.findById(nationConfig.getLeaderUuid()).orElse(null);
            Town capital = townRepository.findByName(nationConfig.getCapitalName()).orElse(null);

            Nation nation = nations.get(nationConfig.getId());

            nation.setCapital(capital);
            nation.setLeader(leader);

            if (capital != null) {
                townService.setTownNation(capital, nation);
            }
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
                .filter(town -> nation.equals(town.getNation()))
                .mapToInt(town -> town.getResidents().size())
                .sum();
    }
}
