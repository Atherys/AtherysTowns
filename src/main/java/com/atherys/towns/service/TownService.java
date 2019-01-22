package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Plot;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
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

    @Inject
    TownService(
            TownsConfig config,
            PlotService plotService,
            TownRepository townRepository,
            PlotRepository plotRepository,
            ResidentRepository residentRepository
    ) {
        this.config = config;
        this.plotService = plotService;
        this.townRepository = townRepository;
        this.plotRepository = plotRepository;
        this.residentRepository = residentRepository;
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
        town.setSpawn(spawn);

        town.addPlot(homePlot);

        townRepository.saveOne(town);

        homePlot.setTown(town);

        plotRepository.saveOne(homePlot);

        leader.setTown(town);

        residentRepository.saveOne(leader);

        // TODO: Permissions

        return town;
    }
}
