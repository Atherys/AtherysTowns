package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
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

    public Town createTown(World world, Transform<World> spawn, Resident leader, Plot homePlot, Text name) {
        Town town = new Town();

        town.setLeader(leader);
        town.setName(name);
        town.setDescription(DEFAULT_TOWN_DESCRIPTION);
        town.setMotd(DEFAULT_TOWN_MOTD);
        town.setColor(DEFAULT_TOWN_COLOR);
        town.setMaxSize(config.DEFAULT_TOWN_MAX_SIZE);
        town.setPvpEnabled(DEFAULT_TOWN_PVP);
        town.setFreelyJoinable(DEFAULT_TOWN_FREELY_JOINABLE);
        town.setWorld(world.getUniqueId());
        town.setBank(UUID.randomUUID());
        town.setSpawn(spawn);

        townRepository.saveOne(town);

        homePlot.setTown(town);
        town.addPlot(homePlot);

        plotRepository.saveOne(homePlot);

        leader.setTown(town);
        town.addResident(leader);

        residentRepository.saveOne(leader);

        permissionService.permit(leader, town, config.DEFAULT_TOWN_LEADER_PERMISSIONS);
        permissionService.permit(town, town, config.DEFAULT_TOWN_RESIDENT_PERMISSIONS);

        return town;
    }

    public Optional<Town> getTownFromName(String townName) {
        return townRepository.findByName(townName);
    }

    public void setTownName(Town town, String name) {
        town.setName(Text.of(name));

        townRepository.saveOne(town);
    }

    public void setTownNation(Town town, Nation nation) {

        // if town is already part of another nation, remove it
        if (town.getNation() != null) {
            town.getNation().removeTown(town);
            nationRepository.saveOne(town.getNation());
        }

        town.setNation(nation);
        nation.addTown(town);

        townRepository.saveOne(town);
        nationRepository.saveOne(nation);
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

    public void removeTown(Town town) {
        permissionService.removeAll(town);

        town.getResidents().forEach(resident -> {
            permissionService.removeAll(resident, town);
            resident.setTown(null);
            residentRepository.saveOne(resident);
        });

        plotRepository.deleteAll(town.getPlots());
        townRepository.deleteOne(town);
    }
}
