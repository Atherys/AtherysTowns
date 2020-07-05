package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.BorderInfo;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
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

@Singleton
public class PlotBorderFacade {
    private final Map<UUID, Boolean> taskStatus = new HashMap<>();

    private final Map<UUID, Set<BorderInfo>> activeTasks = new HashMap<>();

    @Inject
    PlotService plotService;

    @Inject
    PlotSelectionFacade plotSelectionFacade;

    @Inject
    TownsMessagingFacade townMsg;

    public void setPlayerViewBorderStatus(Player player, boolean status) {
        taskStatus.put(player.getUniqueId(), status);
    }

    public boolean isPlayerViewingBorders(Player player) {
        return taskStatus.getOrDefault(player.getUniqueId(), false);
    }

    public void showAllBorders(Player player, Location<World> location) {
        showPlotBorders(player, location);
        showNewPlotSelectionBorders(player, location);
    }

    public void clearBorders(Player player) {
        activeTasks.remove(player.getUniqueId());
    }

    public void refreshBorders(Player player, Location<World> location) {
        clearBorders(player);
        showAllBorders(player, location);
    }

    public void removeSelectionBorder(Player player, Plot plot) {
        Set<BorderInfo> playerBorders = activeTasks.get(player.getUniqueId());
        if (playerBorders != null && playerBorders.size() > 0) {
            playerBorders.forEach(borderInfo -> {
                if (borderInfo.getNECorner() == plot.getNorthEastCorner()) {
                    playerBorders.remove(borderInfo);
                }
            });
        }
    }

    public void addSelectionBorder(Player player, BorderInfo borderInfo) {
        Set<BorderInfo> playerBorders = activeTasks.get(player.getUniqueId());
        if (playerBorders != null) {
            if (playerBorders.stream().noneMatch(borderInfo1 -> borderInfo.getNECorner() == borderInfo1.getNECorner())) {
                playerBorders.add(borderInfo);
            }
        } else {
            Set<BorderInfo> borderSet = new HashSet<>();
            borderSet.add(borderInfo);
            activeTasks.put(player.getUniqueId(), borderSet);
        }
    }

    public void showPlotBorders(Player player, Location<World> newLocation) {
        Optional<Plot> plot = plotService.getPlotByLocation(newLocation);
        if (plot.isPresent() && isPlayerViewingBorders(player)) {
            ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).option(ParticleOptions.COLOR, Color.BLUE)
                    .quantity(8).offset(new Vector3d(0, 4, 0)).build();
            BorderInfo borderInfo = new BorderInfo(effect, player.getUniqueId(), plot.get().getNorthEastCorner(), plot.get().getSouthWestCorner());
            addSelectionBorder(player, borderInfo);
        }
    }

    public void showNewPlotSelectionBorders(Player player, Location<World> location) {
        PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);
        if (selection.isComplete() && isPlayerViewingBorders(player)) {
            ParticleEffect.Builder effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).quantity(8).offset(new Vector3d(0, 4, 0));
            if (plotSelectionFacade.validatePlotSelection(selection, player, false, location)) {
                effect.option(ParticleOptions.COLOR, Color.GREEN);
            } else {
                effect.option(ParticleOptions.COLOR, Color.YELLOW);
            }
            Plot plot = plotService.createPlotFromSelection(selection);
            BorderInfo borderInfo = new BorderInfo(effect.build(), player.getUniqueId(), plot.getNorthEastCorner(), plot.getSouthWestCorner());
            addSelectionBorder(player, borderInfo);
        }
    }

    public void initBorderTask() {
        Task.builder().execute(task -> activeTasks.forEach((uuid, borderInfoSet) -> Sponge.getServer().getPlayer(uuid).ifPresent(player -> borderInfoSet.forEach(borderInfo -> {
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
        })))).intervalTicks(10).submit(AtherysTowns.getInstance());
    }

    public void plotBorderCommand(Player player, boolean state) throws TownsCommandException {
        Text.Builder statusText = Text.builder();
        Text status = state ? Text.of(TextColors.GREEN, "Enabled") : Text.of(TextColors.RED, "Disabled");
        statusText.append(Text.of("Plot border viewing has been ", status));

        if (state == isPlayerViewingBorders(player)) {
            throw new TownsCommandException("Plot border viewing is already ", status);
        }
        townMsg.info(player, statusText.build());

        setPlayerViewBorderStatus(player, state);
        if (state) {
            showAllBorders(player, player.getLocation());
        } else {
            clearBorders(player);
        }
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<Plot> plotTo = plotService.getPlotByLocation(to.getLocation());
        Optional<Plot> plotFrom = plotService.getPlotByLocation(from.getLocation());
        PlotSelection selection = plotSelectionFacade.getCurrentPlotSelection(player);

        if (isPlayerViewingBorders(player)) {
            if ((plotTo.isPresent() && plotFrom.isPresent()) && !plotFrom.equals(plotTo)) {
                refreshBorders(player, to.getLocation());
            }
            if(selection.isComplete()) {
                Plot selectionPlot = plotService.createPlotFromSelection(selection);
                boolean fromInSelection = plotService.isLocationWithinPlot(from.getLocation(), selectionPlot);
                boolean toInSelection = plotService.isLocationWithinPlot(to.getLocation(), selectionPlot);

                if((fromInSelection && !toInSelection) || (!fromInSelection && toInSelection)) {
                    refreshBorders(player, to.getLocation());
                }
            }
            if(!plotFrom.isPresent() && plotTo.isPresent()) {
                refreshBorders(player, to.getLocation());
            }
            if (plotFrom.isPresent() && !plotTo.isPresent()) {
                removeSelectionBorder(player, plotFrom.get());
            }
        }
    }
}
