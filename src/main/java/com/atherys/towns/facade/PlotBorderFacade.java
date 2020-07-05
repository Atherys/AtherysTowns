package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.plot.PlotSelection;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PlotBorderFacade {
    private final Map<UUID, Boolean> taskStatus = new HashMap<>();

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

    public void hideAllBorders(Player player) {
        removeSelectionBorder(player.getUniqueId() + "Selection");
        removeSelectionBorder(player.getUniqueId() + "ShowBorders");
    }

    public void removeSelectionBorder(String name) {
        Sponge.getScheduler().getTasksByName(name).forEach(Task::cancel);
    }

    public void showPlotBorders(Player player, Location<World> newLocation) {
        Optional<Plot> plot = plotService.getPlotByLocation(newLocation);
        if (plot.isPresent() && isPlayerViewingBorders(player)) {
            ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).option(ParticleOptions.COLOR, Color.BLUE)
                    .quantity(8).offset(new Vector3d(0, 4, 0)).build();
            renderSelectionBorder(player, effect, plot.get().getNorthEastCorner(), plot.get().getSouthWestCorner(), "ShowBorders");
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
            renderSelectionBorder(player, effect.build(), plot.getNorthEastCorner(), plot.getSouthWestCorner(), "Selection");
        }
    }

    public void renderSelectionBorder(Player player, ParticleEffect effect, Vector2i NECorner, Vector2i SWCorner, String name) {
        Sponge.getScheduler().getTasksByName(player.getUniqueId() + name).forEach(Task::cancel);

        int xLength = MathUtils.getXLength(NECorner, SWCorner);
        int zLength = MathUtils.getZLength(NECorner, SWCorner);
        Vector3d particleLocationNE = new Vector3d(NECorner.getX(), player.getPosition().getFloorY(), NECorner.getY());
        Vector3d particleLocationSW = new Vector3d(SWCorner.getX(), player.getPosition().getFloorY(), SWCorner.getY());

        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(task -> {
            player.spawnParticles(effect, particleLocationNE.add(1, 0, 0));
            player.spawnParticles(effect, particleLocationSW.add(0, 0, 1));
            for (int i = 0; i <= zLength; i++) {
                player.spawnParticles(effect, particleLocationNE.add(1, 0, i + 1));
                player.spawnParticles(effect, particleLocationSW.sub(0, 0, i));
            }
            for (int i = 0; i <= xLength; i++) {
                player.spawnParticles(effect, particleLocationNE.sub(i, 0, 0));
                player.spawnParticles(effect, particleLocationSW.add(i + 1, 0, 1));
            }

            if (!taskStatus.get(player.getUniqueId())) {
                task.cancel();
            }
        }).intervalTicks(10).name(player.getUniqueId() + name).submit(AtherysTowns.getInstance());
    }

    public void plotBorderCommand(Player player, boolean state) {
        Text.Builder statusText = Text.builder();
        statusText.append(Text.of("Plot border viewing has been "));
        statusText.append(state ? Text.of(TextColors.GREEN, "Enabled") : Text.of(TextColors.RED, "Disabled"));
        townMsg.info(player, statusText.build());

        setPlayerViewBorderStatus(player, state);
        if (state) {
            showAllBorders(player, player.getLocation());
        } else {
            hideAllBorders(player);
        }
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Plot plotTo = plotService.getPlotByLocation(to.getLocation()).orElse(new Plot());
        Plot plotFrom = plotService.getPlotByLocation(from.getLocation()).orElse(new Plot());

        if (isPlayerViewingBorders(player)) {
            if (from.getLocation().getY() != to.getLocation().getY() || plotFrom != plotTo) {
                showAllBorders(player, to.getLocation());
            }
            if (plotFrom.getNorthEastCorner() != null && plotTo.getNorthEastCorner() == null) {
                removeSelectionBorder(player.getUniqueId() + "ShowBorders");
            }
        }
    }

}
