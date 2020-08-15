package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.BorderInfo;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.plot.PlotSelection;
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
    TownsMessagingFacade townMsg;

    public boolean isPlayerViewingBorders(Player player) {
        return borderViewers.getOrDefault(player.getUniqueId(), false);
    }

    public void showAllBorders(Player player, Location<World> location) {
        showPlotBorders(player, location);
        showNewPlotSelectionBorders(player, location);
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
        Optional<Plot> plot = plotService.getPlotByLocation(newLocation);
        if (plot.isPresent() && isPlayerViewingBorders(player)) {
            BorderInfo borderInfo = new BorderInfo(blueWalls, player.getUniqueId(), plot.get().getNorthEastCorner(), plot.get().getSouthWestCorner());
            addSelectionBorder(player, borderInfo);
        }
    }

    public void showNewPlotSelectionBorders(Player player, Location<World> location) {
        PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);
        if (selection.isComplete() && isPlayerViewingBorders(player)) {
            ParticleEffect effect = plotSelectionFacade.validatePlotSelection(selection, player, false, location) ? greenWalls : yellowWalls;
            Plot plot = plotService.createPlotFromSelection(selection);
            BorderInfo borderInfo = new BorderInfo(effect, player.getUniqueId(), plot.getNorthEastCorner(), plot.getSouthWestCorner());
            addSelectionBorder(player, borderInfo);
        }
    }

    public void initBorderTask() {
        Task.builder().execute(task -> activeBorders.forEach((uuid, borderInfoSet) -> {
            Sponge.getServer().getPlayer(uuid).ifPresent(player -> {
                borderInfoSet.forEach(borderInfo -> {
                    int xLength = MathUtils.getXLength(borderInfo.getNECorner(), borderInfo.getSWCorner());
                    int zLength = MathUtils.getZLength(borderInfo.getNECorner(), borderInfo.getSWCorner());
                    Vector3d particleLocationNE = new Vector3d(borderInfo.getNECorner().getX(), player.getPosition().getFloorY(), borderInfo.getNECorner().getY());
                    Vector3d particleLocationSW = new Vector3d(borderInfo.getSWCorner().getX(), player.getPosition().getFloorY(), borderInfo.getSWCorner().getY());

                    player.spawnParticles(borderInfo.getEffect(), particleLocationNE.add(1, 0, 0));
                    player.spawnParticles(borderInfo.getEffect(), particleLocationSW.add(0, 0, 1));
                    for (int i = 0; i <= zLength; i++) {
                        player.spawnParticles(borderInfo.getEffect(), particleLocationNE.add(1, 0, i + 1));
                        player.spawnParticles(borderInfo.getEffect(), particleLocationSW.sub(0, 0, i));
                    }
                    for (int i = 0; i <= xLength; i++) {
                        player.spawnParticles(borderInfo.getEffect(), particleLocationNE.sub(i, 0, 0));
                        player.spawnParticles(borderInfo.getEffect(), particleLocationSW.add(i + 1, 0, 1));
                    }
                });
            });
        })).intervalTicks(10).submit(AtherysTowns.getInstance());
    }

    public void setPlayerViewBorderStatus(Player player, boolean state) throws TownsCommandException {
        Text.Builder statusText = Text.builder();
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
        if (isPlayerViewingBorders(player)) {
            Optional<Plot> plotTo = plotService.getPlotByLocation(to.getLocation());
            Optional<Plot> plotFrom = plotService.getPlotByLocation(from.getLocation());
            PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);

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

            //If you have a complete plot selection
            if (selection.isComplete()) {
                Plot selectionPlot = plotService.createPlotFromSelection(selection);
                boolean fromInSelection = plotService.isLocationWithinPlot(from.getLocation(), selectionPlot);
                boolean toInSelection = plotService.isLocationWithinPlot(to.getLocation(), selectionPlot);

                //If moving from a selection to not a selection, or from not a selection to a selection, then refresh the selection.
                //This will not fire if you are remaining within your selection
                if ((fromInSelection && !toInSelection) || (!fromInSelection && toInSelection)) {
                    refreshBorders(player, to.getLocation());
                }
            }
        }
    }
}
