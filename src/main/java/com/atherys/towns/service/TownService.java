package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class TownService {

    public static final Text DEFAULT_TOWN_DESCRIPTION = Text.of("Town description.");

    public static final Text DEFAULT_TOWN_MOTD = Text.of("Message of the day.");

    public static final TextColor DEFAULT_TOWN_COLOR = TextColors.RESET;

    public static final boolean DEFAULT_TOWN_PVP = false;

    public static final boolean DEFAULT_TOWN_FREELY_JOINABLE = false;

    private TownsConfig config;

    private PlotService plotService;

    private TownRepository townRepository;

    private PlotRepository plotRepository;

    private ResidentRepository residentRepository;

    private TownsPermissionService townsPermissionService;

    private RoleService roleService;

    @Inject
    TownService(
            TownsConfig config,
            PlotService plotService,
            TownRepository townRepository,
            PlotRepository plotRepository,
            ResidentRepository residentRepository,
            TownsPermissionService townsPermissionService,
            RoleService roleService
    ) {
        this.config = config;
        this.plotService = plotService;
        this.townRepository = townRepository;
        this.plotRepository = plotRepository;
        this.residentRepository = residentRepository;
        this.townsPermissionService = townsPermissionService;
        this.roleService = roleService;
    }

    public Town createTown(World world, Transform<World> spawn, User leaderUser, Resident leader, Plot homePlot, String name) {
        Town town = new Town();

        town.setLeader(leader);
        town.setName(name);
        town.setDescription(DEFAULT_TOWN_DESCRIPTION);
        town.setMotd(DEFAULT_TOWN_MOTD);
        town.setColor(DEFAULT_TOWN_COLOR);
        town.setMaxSize(config.TOWN.DEFAULT_TOWN_MAX_SIZE);
        town.setPvpEnabled(DEFAULT_TOWN_PVP);
        town.setFreelyJoinable(DEFAULT_TOWN_FREELY_JOINABLE);
        town.setWorld(world.getUniqueId());
        town.setBank(UUID.randomUUID());
        if (AtherysTowns.economyIsEnabled()) {
           AtherysCore.getEconomyService().get().getOrCreateAccount(town.getBank().toString());
        }
        town.setSpawn(spawn);

        townRepository.saveOne(town);

        homePlot.setTown(town);
        town.addPlot(homePlot);

        plotRepository.saveOne(homePlot);

        leader.setTown(town);
        town.addResident(leader);

        residentRepository.saveOne(leader);

        roleService.addTownRole(leaderUser, town, config.TOWN.TOWN_LEADER_ROLE);
        roleService.addTownRole(leaderUser, town, config.TOWN.TOWN_DEFAULT_ROLE);

        return town;
    }

    public Optional<Town> getTownFromName(String townName) {
        return townRepository.findByName(townName);
    }

    public void setTownName(Town town, String name) {
        town.setName(name);
        townRepository.saveOne(town);
    }

    public void setTownMotd(Town town, Text motd) {
        town.setMotd(motd);
        townRepository.saveOne(town);
    }

    public void setTownColor(Town town, TextColor textColor) {
        town.setColor(textColor);
        townRepository.saveOne(town);
    }

    public void setTownDescription(Town town, Text desc) {
        town.setDescription(desc);
        townRepository.saveOne(town);
    }

    public void setTownPvp(Town town, boolean pvp) {
        town.setPvpEnabled(pvp);
        townRepository.saveOne(town);
    }

    public void setTownNation(Town town, Nation nation) {
        Set<String> ids = town.getResidents().stream()
                .map(resident -> resident.getId().toString())
                .collect(Collectors.toSet());

        Set<Context> nationContext = town.getNation() == null ? null : townsPermissionService.getContextForNation(town.getNation());

        Sponge.getServiceManager().provideUnchecked(PermissionService.class)
                .getUserSubjects()
                .applyToAll(subject -> {
                    if (town.getNation() != null) {
                        townsPermissionService.clearPermissions(subject, nationContext);
                    }
                    roleService.addNationRole(subject, nation, nation.getDefaultNationRole());
                }, ids);

        town.setNation(nation);
        townRepository.saveOne(town);
    }

    public void setTownJoinable(Town town, boolean joinable) {
        town.setFreelyJoinable(joinable);
        townRepository.saveOne(town);
    }

    public void setTownSpawn(Town town, Transform<World> spawn) {
        town.setSpawn(spawn);
        townRepository.saveOne(town);
    }

    public void removePlotFromTown(Town town, Plot plot) {
        town.removePlot(plot);

        townRepository.saveOne(town);
        plotRepository.deleteOne(plot);
    }

    public void claimPlotForTown(Plot plot, Town town) {
        town.addPlot(plot);
        plot.setTown(town);

        plotRepository.saveOne(plot);
        townRepository.saveOne(town);
    }

    public int getTownSize(Town town) {
        int size = 0;

        for (Plot plot : town.getPlots()) {
            size += plotService.getPlotArea(plot);
        }

        return size;
    }

    public void increaseTownSize(Town town, int amount) {
        town.setMaxSize(town.getMaxSize() + amount);
        townRepository.saveOne(town);
    }
    
    public void decreaseTownSize(Town town, int amount) {
        town.setMaxSize(town.getMaxSize() - amount);
        townRepository.saveOne(town);
    }

    public void removeTown(Town town) {
        Set<Context> townContext = townsPermissionService.getContextsForTown(town);
        Set<String> ids = new HashSet<>();

        town.getResidents().forEach(resident -> {
            resident.setTown(null);
            ids.add(resident.getId().toString());
        });

        Sponge.getServiceManager().provideUnchecked(PermissionService.class)
                .getUserSubjects()
                .applyToAll(subject -> townsPermissionService.clearPermissions(subject, townContext), ids);

        residentRepository.saveAll(town.getResidents());
        plotRepository.deleteAll(town.getPlots());
        townRepository.deleteOne(town);
    }

    public void addResidentToTown(User user, Resident resident, Town town) {
        town.addResident(resident);
        resident.setTown(town);
        roleService.addTownRole(user, town, config.TOWN.TOWN_DEFAULT_ROLE);

        if (town.getNation() != null) {
            roleService.addNationRole(user, town.getNation(), town.getNation().getDefaultNationRole());
        }

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);
    }

    public void removeResidentFromTown(User user, Resident resident, Town town) {
        town.removeResident(resident);
        resident.setTown(null);
        townsPermissionService.clearPermissions(user, town);

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);
    }
}
