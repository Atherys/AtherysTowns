package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.NationRepository;
import com.atherys.towns.persistence.PlotRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    private PermissionService permissionService;

    private NationRepository nationRepository;

    @Inject
    TownService(
            TownsConfig config,
            PlotService plotService,
            TownRepository townRepository,
            PlotRepository plotRepository,
            ResidentRepository residentRepository,
            PermissionService permissionService,
            NationRepository nationRepository
    ) {
        this.config = config;
        this.plotService = plotService;
        this.townRepository = townRepository;
        this.plotRepository = plotRepository;
        this.residentRepository = residentRepository;
        this.permissionService = permissionService;
        this.nationRepository = nationRepository;
    }

    public Town createTown(World world, Transform<World> spawn, Resident leader, Plot homePlot, String name) {
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

        permissionService.permit(leader, town, permissionService.getTownLeaderRole());

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

    public void setTownNation(Town town, NationConfig nation) {

        // if town is already part of another nation, remove it
        if (town.getNation() != null) {
            town.getNation().removeTown(town);
            town.getResidents().forEach(resident -> permissionService.removeAll(resident, nation));
            nationRepository.saveOne(town.getNation());
        }

        town.getResidents().forEach(resident -> {
            permissionService.permit(resident, nation, config.DEFAULT_NATION_RESIDENT_PERMISSIONS);
        });

        town.setNation(nation);
        nation.addTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
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
        CompletableFuture<Void> complete = permissionService.removeAll(town);

        complete.thenAccept(__ -> {
            town.getResidents().forEach(resident -> {
                permissionService.removeAll(resident, town);
                resident.setTown(null);
                residentRepository.saveOne(resident);
            });
        });

        plotRepository.deleteAll(town.getPlots());
        townRepository.deleteOne(town);
    }

    public void addResidentToTown(Resident resident, Town town) {
        town.addResident(resident);
        resident.setTown(town);
        permissionService.permit(resident, town, config.DEFAULT_TOWN_RESIDENT_PERMISSIONS);

        if (town.getNation() != null) {
            permissionService.permit(resident, town.getNation(), config.DEFAULT_NATION_RESIDENT_PERMISSIONS);
        }

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);
    }

    public void removeResidentFromTown(Resident resident, Town town) {
        town.removeResident(resident);
        resident.setTown(null);
        permissionService.removeAll(resident, town);

        if (town.getNation() != null) {
            permissionService.removeAll(resident, town.getNation());
        }

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);
    }
}
