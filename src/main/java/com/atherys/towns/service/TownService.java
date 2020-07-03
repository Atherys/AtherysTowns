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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
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

    private ResidentService residentService;

    private TownsPermissionService townsPermissionService;

    private RoleService roleService;

    @Inject
    TownService(
            TownsConfig config,
            PlotService plotService,
            TownRepository townRepository,
            PlotRepository plotRepository,
            ResidentRepository residentRepository,
            ResidentService residentService,
            TownsPermissionService townsPermissionService,
            RoleService roleService
    ) {
        this.config = config;
        this.plotService = plotService;
        this.townRepository = townRepository;
        this.plotRepository = plotRepository;
        this.residentRepository = residentRepository;
        this.residentService = residentService;
        this.townsPermissionService = townsPermissionService;
        this.roleService = roleService;
    }

    public Town createTown(Player leader, Plot homePlot, String name) {
        Town town = new Town();
        Nation nation = null;
        Resident resLeader = residentService.getOrCreate(leader);

        if (resLeader.getTown() != null) {
            nation = resLeader.getTown().getNation();
            removeResidentFromTown(leader, resLeader, resLeader.getTown());
        }

        town.setLeader(resLeader);
        town.setName(name);
        town.setDescription(DEFAULT_TOWN_DESCRIPTION);
        town.setMotd(DEFAULT_TOWN_MOTD);
        town.setColor(DEFAULT_TOWN_COLOR);
        town.setMaxSize(config.TOWN.DEFAULT_TOWN_MAX_SIZE);
        town.setPvpEnabled(DEFAULT_TOWN_PVP);
        town.setFreelyJoinable(DEFAULT_TOWN_FREELY_JOINABLE);
        town.setWorld(leader.getWorld().getUniqueId());
        town.setBank(UUID.randomUUID());
        town.setNation(nation);
        if (AtherysTowns.economyIsEnabled()) {
            AtherysCore.getEconomyService().get().getOrCreateAccount(town.getBank().toString());
        }
        town.setSpawn(leader.getTransform());

        homePlot.setTown(town);
        town.addPlot(homePlot);


        resLeader.setTown(town);
        town.addResident(resLeader);

        townRepository.saveOne(town);
        plotRepository.saveOne(homePlot);

        residentRepository.saveOne(resLeader);

        roleService.addTownRole(leader, town, config.TOWN.TOWN_LEADER_ROLE);
        roleService.addTownRole(leader, town, config.TOWN.TOWN_DEFAULT_ROLE);

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

        AtherysTowns.getInstance().getLogger().info(ids.toString());

        Set<Context> nationContext = town.getNation() == null ? null : townsPermissionService.getContextForNation(town.getNation());

        Sponge.getServiceManager().provideUnchecked(PermissionService.class)
                .getUserSubjects()
                .applyToAll(subject -> {
                    if (town.getNation() != null) {
                        townsPermissionService.clearPermissions(subject, nationContext);
                    }
                    roleService.addNationRole(subject, nation, config.DEFAULT_ROLE);
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

        removePlotFromGraph(town, plot);

        townRepository.saveOne(town);
        plotRepository.deleteOne(plot);
    }

    public void claimPlotForTown(Plot plot, Town town) {
        town.addPlot(plot);
        plot.setTown(town);

        plotRepository.saveOne(plot);
        townRepository.saveOne(town);

        addPlotToGraph(town, plot);
    }

    public void generatePlotGraph(Town town) {
        Map<Plot, Set<Plot>> adjList = new HashMap<>();
        for (Plot plota : town.getPlots()) {
            for (Plot plotb : town.getPlots()) {
                // Plots can't be adjacent to themselves
                if (plota == plotb) continue;

                Set<Plot> plotaNeighbours = adjList.computeIfAbsent(plota, k -> new HashSet<>());
                Set<Plot> plotbNeighbours = adjList.computeIfAbsent(plotb, k -> new HashSet<>());

                // If we have already determined this is a neighbour in a previous iteration
                if (plotaNeighbours.contains(plotb)) continue;

                // Other check if we have neighbours
                if (plotService.plotsBorder(plota, plotb)) {
                    plotaNeighbours.add(plotb);
                    plotbNeighbours.add(plota);
                }
            }
        }
        town.setPlotGraphAdjList(adjList);
    }

    public void addPlotToGraph(Town town, Plot newPlot) {
        Map<Plot, Set<Plot>> adjList = town.getPlotGraphAdjList();
        if (adjList == null) return;

        for (Plot existingPlot : town.getPlots()) {

            // Plots can't be adjacent to themselves
            if (newPlot == existingPlot) continue;

            Set<Plot> newPlotNeighbours = adjList.computeIfAbsent(newPlot, k -> new HashSet<>());
            Set<Plot> existingPlotNeighbours = adjList.computeIfAbsent(existingPlot, k -> new HashSet<>());
            if (plotService.plotsBorder(newPlot, existingPlot)) {
                newPlotNeighbours.add(existingPlot);
                existingPlotNeighbours.add(newPlot);
            }
        }
    }

    public void removePlotFromGraph(Town town, Plot removedPlot) {
        Map<Plot, Set<Plot>> adjList = town.getPlotGraphAdjList();
        if (adjList == null) return;

        for (Plot existingPlot : town.getPlots()) {
            Set<Plot> existingPlotNeighbours = adjList.computeIfAbsent(existingPlot, k -> new HashSet<>());
            existingPlotNeighbours.remove(removedPlot);
        }
        adjList.remove(removedPlot);
    }

    /**
     * Checks if the removal of a plot would result in orphaned plots
     * <p>
     * This is done by doing a Depth First Search starting with the node to be
     * removed. Counting the number of children the root node has in the DFS we can
     * determine if it is a articulation point.
     *
     * @param town       The Town to check
     * @param targetPlot The plot to be removed
     * @return true if removal of plot results in orphaned plots otherwise false
     */
    public boolean checkPlotRemovalCreatesOrphans(Town town, Plot targetPlot) {
        // We need to do a full DFS, and work out if the root node > 1 children

        Map<Plot, Boolean> visited = new HashMap<>();

        // As we only care about whether or not the root node is an AP (articulation point)
        // Only need to track the number of children the root node has
        int rootChildren = 0;

        Map<Plot, Set<Plot>> adjList = town.getPlotGraphAdjList();

        if (adjList == null) {
            generatePlotGraph(town);
            adjList = town.getPlotGraphAdjList();
        }

        // Stack of possible edges to visit, edges of defined as an array of [parent, child]
        // We keep track of parent so that we can can count the children of root
        Stack<Tuple<Plot, Plot>> stack = new Stack<>();

        visited.put(targetPlot, true);

        Set<Plot> test = adjList.get(targetPlot);

        for (Plot child : adjList.get(targetPlot)) {
            stack.push(Tuple.of(targetPlot, child));
        }

        while (!stack.empty()) {
            Tuple<Plot, Plot> t = stack.pop();
            Plot parent = t.getFirst();
            Plot plot = t.getSecond();

            if (!visited.getOrDefault(plot, false)) {
                // If we are choosing to use this edge, mark the node as visited
                visited.put(plot, true);
                if (parent == targetPlot) rootChildren++;
                if (rootChildren >= 2) return true;
            }

            for (Plot child : adjList.get(plot)) {
                if (!visited.getOrDefault(child, false)) {
                    stack.push(Tuple.of(plot, child));
                }
            }
        }

        return false;
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
            roleService.addNationRole(user, town.getNation(), config.DEFAULT_ROLE);
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
