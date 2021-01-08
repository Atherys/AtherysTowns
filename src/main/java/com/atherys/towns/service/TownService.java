package com.atherys.towns.service;

import com.atherys.core.AtherysCore;
import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.config.TaxConfig;
import com.atherys.towns.api.event.ResidentEvent;
import com.atherys.towns.api.event.TownEvent;
import com.atherys.towns.facade.TownsMessagingFacade;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.TownPlotRepository;
import com.atherys.towns.persistence.ResidentRepository;
import com.atherys.towns.persistence.TownRepository;
import com.atherys.towns.util.MathUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class TownService {

    public static final Text DEFAULT_TOWN_DESCRIPTION = Text.of("Town description.");

    public static final Text DEFAULT_TOWN_MOTD = Text.of("Message of the day.");

    public static final TextColor DEFAULT_TOWN_COLOR = TextColors.RESET;

    public static final boolean DEFAULT_TOWN_PVP = false;

    public static final boolean DEFAULT_TOWN_FREELY_JOINABLE = false;

    private final TownsConfig config;

    private final PlotService plotService;

    private final NationService nationService;

    private final TownRepository townRepository;

    private TownPlotRepository townPlotRepository;

    private final ResidentRepository residentRepository;

    private final ResidentService residentService;

    private final TownsPermissionService townsPermissionService;

    private final RoleService roleService;

    @Inject
    TownService(
            TownsConfig config,
            PlotService plotService,
            TownRepository townRepository,
            TownPlotRepository townPlotRepository,
            NationService nationService,
            ResidentRepository residentRepository,
            ResidentService residentService,
            TownsPermissionService townsPermissionService,
            RoleService roleService
    ) {
        this.config = config;
        this.plotService = plotService;
        this.townRepository = townRepository;
        this.townPlotRepository = townPlotRepository;
        this.residentRepository = residentRepository;
        this.nationService = nationService;
        this.residentService = residentService;
        this.townsPermissionService = townsPermissionService;
        this.roleService = roleService;
    }

    public Town createTown(Player leader, TownPlot homePlot, String name, @Nullable Nation nation) {
        Town town = new Town();
        Resident resLeader = residentService.getOrCreate(leader);

        if (resLeader.getTown() != null) {
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
        town.setLastTaxDate(LocalDateTime.now());
        town.setTaxFailedCount(0);
        town.setDebt(0);
        town.setBank(UUID.randomUUID());
        town.setTaxable(true);

        if (nation != null) {
            nationService.addTown(nation, town);
        }

        if (AtherysTowns.economyIsEnabled()) {
            AtherysCore.getEconomyService().get().getOrCreateAccount(town.getBank());
        }

        town.setSpawn(leader.getTransform());
        homePlot.setName(Text.of("HomePlot"));

        homePlot.setTown(town);
        town.addPlot(homePlot);

        resLeader.setTown(town);
        town.addResident(resLeader);

        townRepository.saveOne(town);
        townPlotRepository.saveOne(homePlot);

        residentRepository.saveOne(resLeader);

        roleService.addTownRole(leader, town, config.TOWN.TOWN_LEADER_ROLE);
        roleService.addTownRole(leader, town, config.TOWN.TOWN_DEFAULT_ROLE);

        Sponge.getEventManager().post(new TownEvent.Created(town));

        return town;
    }

    public Optional<Town> getTownFromName(String townName) {
        return townRepository.findByName(townName);
    }

    public void setTownLeader(Town town, Resident resident) {
        Resident previousLeader = town.getLeader();

        town.setLeader(resident);
        townRepository.saveOne(town);

        if (previousLeader.isFake()) {
            removeResidentFromTown(null, previousLeader, town);
        }
    }

    public void setTownName(Town town, String name) {
        String oldName = town.getName();

        town.setName(name);
        townRepository.saveOne(town);

        Sponge.getEventManager().post(new TownEvent.Renamed(town, oldName, name));
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

    public void setTownTaxFailCount(Town town, int count) {
        town.setTaxFailedCount(count);
        townRepository.saveOne(town);
    }

    public void setTownDebt(Town town, double debt) {
        town.setDebt(debt);
        townRepository.saveOne(town);
    }

    public void addFailedTaxOccurrence(Town town) {
        town.setTaxFailedCount(town.getTaxFailedCount() + 1);
        townRepository.saveOne(town);
    }

    public void addTownDebt(Town town, double debt) {
        town.setDebt(town.getDebt() + debt);
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
                    roleService.addNationRole(subject, nation, config.NATION.DEFAULT_ROLE);
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

    public void setTownLastRaidCreationDate(Town town, LocalDateTime dateTime) {
        town.setLastRaidCreationDate(dateTime);
        townRepository.saveOne(town);
    }

    public void removePlotFromTown(Town town, TownPlot plot) {
        town.removePlot(plot);

        removePlotFromGraph(town, plot);

        townRepository.saveOne(town);
        townPlotRepository.deleteOne(plot);
    }

    public void claimPlotForTown(TownPlot plot, Town town) {
        plot.setName(Text.of("Plot #", town.getPlots().size()));
        town.addPlot(plot);
        plot.setTown(town);

        townPlotRepository.saveOne(plot);
        townRepository.saveOne(town);

        addPlotToGraph(town, plot);
    }

    public void generatePlotGraph(Town town) {
        Map<TownPlot, Set<TownPlot>> adjList = new HashMap<>();
        for (TownPlot plota : town.getPlots()) {
            for (TownPlot plotb : town.getPlots()) {
                // Plots can't be adjacent to themselves
                if (plota == plotb) continue;

                Set<TownPlot> plotaNeighbours = adjList.computeIfAbsent(plota, k -> new HashSet<>());
                Set<TownPlot> plotbNeighbours = adjList.computeIfAbsent(plotb, k -> new HashSet<>());

                // If we have already determined this is a neighbour in a previous iteration
                if (plotaNeighbours.contains(plotb)) continue;

                // Other check if we have neighbours
                if (MathUtils.borders(plota, plotb)) {
                    plotaNeighbours.add(plotb);
                    plotbNeighbours.add(plota);
                }
            }
        }
        town.setPlotGraphAdjList(adjList);
    }

    public void addPlotToGraph(Town town, TownPlot newPlot) {
        Map<TownPlot, Set<TownPlot>> adjList = town.getPlotGraphAdjList();
        if (adjList == null) return;

        for (TownPlot existingPlot : town.getPlots()) {

            // Plots can't be adjacent to themselves
            if (newPlot == existingPlot) continue;

            Set<TownPlot> newPlotNeighbours = adjList.computeIfAbsent(newPlot, k -> new HashSet<>());
            Set<TownPlot> existingPlotNeighbours = adjList.computeIfAbsent(existingPlot, k -> new HashSet<>());
            if (MathUtils.borders(newPlot, existingPlot)) {
                newPlotNeighbours.add(existingPlot);
                existingPlotNeighbours.add(newPlot);
            }
        }
    }

    public void removePlotFromGraph(Town town, TownPlot removedPlot) {
        Map<TownPlot, Set<TownPlot>> adjList = town.getPlotGraphAdjList();
        if (adjList == null) return;

        for (TownPlot existingPlot : town.getPlots()) {
            Set<TownPlot> existingPlotNeighbours = adjList.computeIfAbsent(existingPlot, k -> new HashSet<>());
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
    public boolean checkPlotRemovalCreatesOrphans(Town town, TownPlot targetPlot) {
        // We need to do a full DFS, and work out if the root node > 1 children

        Map<TownPlot, Boolean> visited = new HashMap<>();

        // As we only care about whether or not the root node is an AP (articulation point)
        // Only need to track the number of children the root node has
        int rootChildren = 0;

        Map<TownPlot, Set<TownPlot>> adjList = town.getPlotGraphAdjList();

        if (adjList == null) {
            generatePlotGraph(town);
            adjList = town.getPlotGraphAdjList();
        }

        // Stack of possible edges to visit, edges of defined as an array of [parent, child]
        // We keep track of parent so that we can can count the children of root
        Stack<Tuple<TownPlot, TownPlot>> stack = new Stack<>();

        visited.put(targetPlot, true);

        for (TownPlot child : adjList.get(targetPlot)) {
            stack.push(Tuple.of(targetPlot, child));
        }

        while (!stack.empty()) {
            Tuple<TownPlot, TownPlot> t = stack.pop();
            TownPlot parent = t.getFirst();
            TownPlot plot = t.getSecond();

            if (!visited.getOrDefault(plot, false)) {
                // If we are choosing to use this edge, mark the node as visited
                visited.put(plot, true);
                if (parent == targetPlot) rootChildren++;
                if (rootChildren >= 2) return true;
            }

            for (TownPlot child : adjList.get(plot)) {
                if (!visited.getOrDefault(child, false)) {
                    stack.push(Tuple.of(plot, child));
                }
            }
        }

        return false;
    }

    public int getTownSize(Town town) {
        int size = 0;

        for (TownPlot plot : town.getPlots()) {
            size += MathUtils.getArea(plot);
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
            Sponge.getEventManager().post(new ResidentEvent.LeftTown(resident, town));
        });

        Sponge.getServiceManager().provideUnchecked(PermissionService.class)
                .getUserSubjects()
                .applyToAll(subject -> townsPermissionService.clearPermissions(subject, townContext), ids);

        if (town.getNation() != null) {
            nationService.removeTown(town.getNation(), town);
        }

        residentRepository.saveAll(town.getResidents());
        townPlotRepository.deleteAll(town.getPlots());
        townRepository.deleteOne(town);

        Sponge.getEventManager().post(new TownEvent.Removed(town));
    }

    public void addResidentToTown(@Nullable User user, Resident resident, Town town) {
        town.addResident(resident);
        resident.setTown(town);

        if (user != null) {
            roleService.addTownRole(user, town, config.TOWN.TOWN_DEFAULT_ROLE);

            if (town.getNation() != null) {
                roleService.addNationRole(user, town.getNation(), config.NATION.DEFAULT_ROLE);
            }

            townsPermissionService.updateContexts(user, resident);
        }

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);

        Sponge.getEventManager().post(new ResidentEvent.JoinedTown(resident, town));
    }

    public void removeResidentFromTown(@Nullable User user, Resident resident, Town town) {
        town.getResidents().removeIf(r -> r.getId().equals(resident.getId()));
        resident.setTown(null);

        if (user != null) {
            townsPermissionService.clearPermissions(user, town);
            townsPermissionService.updateContexts(user, resident);
        }

        townRepository.saveOne(town);
        residentRepository.saveOne(resident);

        if (resident.isFake()) {
            residentRepository.deleteOne(resident);
        }

        Sponge.getEventManager().post(new ResidentEvent.LeftTown(resident, town));
    }

    public Collection<Town> fetchAllTowns() {
        return townRepository.getAll();
    }

    public void setTownTaxable(Town town, boolean taxable) {
        town.setTaxable(taxable);
    }
}
