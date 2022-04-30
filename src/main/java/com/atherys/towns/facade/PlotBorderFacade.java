package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.BorderInfo;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.model.entity.TownPlot;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class PlotBorderFacade {
    private final Map<UUID, Boolean> borderViewers = new ConcurrentHashMap<>();
    private final Map<UUID, Set<BorderInfo>> activeBorders = new ConcurrentHashMap<>();

    private final ParticleEffect.Builder wallEffectBuilder = ParticleEffect.builder()
            .type(ParticleTypes.REDSTONE_DUST)
            .offset(new Vector3d(0, 4, 0))
            .quantity(8);

    private final ParticleEffect blueWalls = wallEffectBuilder
            .option(ParticleOptions.COLOR, Color.BLUE)
            .build();

    private final ParticleEffect greenWalls = wallEffectBuilder
            .option(ParticleOptions.COLOR, Color.GREEN)
            .build();

    private final ParticleEffect yellowWalls = wallEffectBuilder
            .option(ParticleOptions.COLOR, Color.YELLOW)
            .build();

    @Inject
    PlotService plotService;

    @Inject
    PlotSelectionFacade plotSelectionFacade;

    @Inject
    TownFacade townFacade;

    @Inject
    TownsMessagingFacade townMsg;

    @Inject
    private TownsConfig config;

    public boolean isPlayerViewingBorders(Player player) {
        return borderViewers.getOrDefault(player.getUniqueId(), false);
    }

    public void showAllBorders(Player player, Location<World> location) {
        showPlotBorders(player, location);
        if (isPlayerViewingBorders(player)) {
            showNewPlotSelectionBorders(player, location);
        }
    }

    public void clearBorders(Player player) {
        activeBorders.remove(player.getUniqueId());
    }

    public void refreshBorders(Player player, Location<World> location) {
        clearBorders(player);
        showAllBorders(player, location);
    }

    public void removeSelectionBorder(Player player, Plot plot) {
        Set<BorderInfo> playerBorders = activeBorders.get(player.getUniqueId());
        if (playerBorders != null && playerBorders.size() > 0 && plot != null) {
            playerBorders.removeIf(borderInfo -> borderInfo.getNECorner().equals(plot.getNorthEastCorner()));
        }
    }

    public void removeBordersForTown(Town town) {
        for (Plot plot : town.getPlots()) {
            Map<UUID, Set<BorderInfo>> bordersToRemove = new HashMap<>();
            for (Map.Entry<UUID, Set<BorderInfo>> info : activeBorders.entrySet()) {
                Set<BorderInfo> borders = info.getValue().stream().filter(borderInfo -> borderInfo.getNECorner() == plot.getNorthEastCorner()).collect(Collectors.toSet());
                bordersToRemove.put(info.getKey(), borders);
            }
            bordersToRemove.forEach(activeBorders::remove);
        }
    }

    public void addSelectionBorder(Player player, BorderInfo borderInfo) {
        Set<BorderInfo> playerBorders = activeBorders.get(player.getUniqueId());
        if (playerBorders != null) {
            if (playerBorders.stream().noneMatch(borderInfo::equals)) {
                playerBorders.add(borderInfo);
            }
        } else {
            Set<BorderInfo> borderSet = new HashSet<>();
            borderSet.add(borderInfo);
            activeBorders.put(player.getUniqueId(), borderSet);
        }
    }

    public void showPlotBorders(Player player, Location<World> newLocation) {
        Optional<TownPlot> plot = plotService.getTownPlotByLocation(newLocation);
        if (plot.isPresent() && isPlayerViewingBorders(player)) {
            BorderInfo borderInfo = new BorderInfo(plot.get().getNorthEastCorner(), plot.get().getSouthWestCorner(), plot.get().isCuboid(), blueWalls);
            addSelectionBorder(player, borderInfo);
        }
    }

    public void showNewPlotSelectionBorders(Player player, Location<World> location) {
        plotSelectionFacade.getCompletePlotSelection(player).ifPresent(selection -> {
            TownPlot plot = plotService.createTownPlotFromSelection(selection);
            // Only show plots smaller than the max plot area
            int plotArea = MathUtils.getArea(plot);
            if (plotArea < config.TOWN.MAX_PLOT_AREA) {
                ParticleEffect effect = townFacade.isValidNewTownPlot(plot, player, location, false) ? greenWalls : yellowWalls;
                BorderInfo borderInfo = new BorderInfo(plot.getNorthEastCorner(), plot.getSouthWestCorner(), plot.isCuboid(), effect);
                addSelectionBorder(player, borderInfo);
            }
        });
    }

    public void initBorderTask() {
        Task.builder().execute(task -> activeBorders.forEach((uuid, borderInfoSet) ->
            Sponge.getServer().getPlayer(uuid).ifPresent(player ->
                borderInfoSet.forEach(borderInfo -> {
                    int width = MathUtils.getWidth(borderInfo);
                    int height = MathUtils.getHeight(borderInfo);

                    double y = player.getPosition().getY();
                    Vector3d particleLocationNE = new Vector3d(borderInfo.getNECorner().getX(), y, borderInfo.getNECorner().getZ());
                    Vector3d particleLocationSW = new Vector3d(borderInfo.getSWCorner().getX(), y, borderInfo.getSWCorner().getZ());

                    player.spawnParticles(borderInfo.getEffect(), particleLocationNE);
                    player.spawnParticles(borderInfo.getEffect(), particleLocationSW);
                    for (int i = 1; i <= height; i++) {
                        player.spawnParticles(borderInfo.getEffect(), particleLocationNE.add(0, 0, i));
                        player.spawnParticles(borderInfo.getEffect(), particleLocationSW.sub(0, 0, i));
                    }

                    for (int i = 1; i <= width; i++) {
                        player.spawnParticles(borderInfo.getEffect(), particleLocationNE.sub(i, 0, 0));
                        player.spawnParticles(borderInfo.getEffect(), particleLocationSW.add(i, 0, 0));
                    }
                })
            )
        )).intervalTicks(10).submit(AtherysTowns.getInstance());
    }

    public void setPlayerViewBorderStatus(Player player) throws TownsCommandException {
        Text.Builder statusText = Text.builder();
        boolean state = !isPlayerViewingBorders(player);
        Text status = state ? Text.of(TextColors.GREEN, "Enabled") : Text.of(TextColors.RED, "Disabled");
        statusText.append(Text.of("Plot border viewing has been ", status));

        if (state == isPlayerViewingBorders(player)) {
            throw new TownsCommandException("Plot border viewing is already ", status);
        }
        townMsg.info(player, statusText.build());
        borderViewers.put(player.getUniqueId(), state);
        if (state) {
            showAllBorders(player, player.getLocation());
        } else {
            clearBorders(player);
        }
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        if (!isPlayerViewingBorders(player)) return;

        Optional<TownPlot> plotTo = plotService.getTownPlotByLocation(to.getLocation());
        Optional<TownPlot> plotFrom = plotService.getTownPlotByLocation(from.getLocation());

        //If you're leaving a plot, then remove the border
        if (plotFrom.isPresent() && !plotTo.isPresent()) {
            removeSelectionBorder(player, plotFrom.get());
        }

        //If you're entering a plot, then draw a border
        if (!plotFrom.isPresent() && plotTo.isPresent()) {
            refreshBorders(player, to.getLocation());
        }

        //If your from and to locations are both plots, but not the same plot, then remove border and add new border
        if ((plotTo.isPresent() && plotFrom.isPresent()) && !plotFrom.equals(plotTo)) {
            refreshBorders(player, to.getLocation());
        }

        plotSelectionFacade.getCompletePlotSelection(player).ifPresent(selection -> {
            // If you have a complete plot selection
            if (selection.isComplete()) {
                TownPlot selectionPlot = plotService.createTownPlotFromSelection(selection);
                boolean fromInSelection = plotService.isLocationWithinPlot(from.getLocation(), selectionPlot);
                boolean toInSelection = plotService.isLocationWithinPlot(to.getLocation(), selectionPlot);

                //If moving from a selection to not a selection, or from not a selection to a selection, then refresh the selection.
                //This will not fire if you are remaining within your selection
                if ((fromInSelection && !toInSelection) || (!fromInSelection && toInSelection)) {
                    refreshBorders(player, to.getLocation());
                }
            }
        });
    }
}
