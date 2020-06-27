package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class PlotFacade {

    @Inject
    PlotService plotService;

    @Inject
    PermissionFacade permissionFacade;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    ResidentService residentService;

    PlotFacade() {
    }

    public void renamePlotAtPlayerLocation(Player player, Text newName) throws TownsCommandException {
        Plot plot = getPlotAtPlayer(player);

        if (permissionFacade.isPermitted(player, TownPermissions.RENAME_PLOT) ||
                residentService.getOrCreate(player).equals(plot.getOwner())) {

            plotService.setPlotName(plot, newName);
            townsMsg.info(player, "Plot renamed.");
        } else {
            throw new TownsCommandException("You are not permitted to rename this plot.");
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        Plot plot = getPlotAtPlayer(player);
        Resident plotOwner = (plot.getOwner() != null) ? plot.getOwner() : new Resident();
        String ownerName = (plotOwner.getName() != null) ? plotOwner.getName() : "None";
        Text.Builder plotText = Text.builder();

        plotText
                .append(townsMsg.createTownsHeader(plot.getName().toPlain()));

        plotText.append(Text.of(
                DARK_GREEN, "Town: ",
                plot.getTown() == null ? Text.of(RED, "None") : Text.of(GOLD, plot.getTown().getName()),
                Text.NEW_LINE
        ));

        plotText
                .append(Text.of(DARK_GREEN, "Owner: ", GOLD, ownerName, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Size: ", GOLD, plotService.getPlotArea(plot), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point A: ", GOLD, "x: ", plot.getSouthWestCorner().getX(), ", z: ", plot.getSouthWestCorner().getY(), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point B: ", GOLD, "x: ", plot.getNorthEastCorner().getX(), ", z: ", plot.getNorthEastCorner().getY()));

        player.sendMessage(plotText.build());
    }

    public void grantPlayerPlotAtPlayerLocation(Player player, User target) throws TownsCommandException {
        Plot plot = getPlotAtPlayer(player);

        plotService.setPlotOwner(plot, residentService.getOrCreate(target));
        townsMsg.info(player, "Granted the plot ", GOLD, plot.getName(), DARK_GREEN, " to ", GOLD, target.getName(), DARK_GREEN, ".");
    }

    private Plot getPlotAtPlayer(Player player) throws TownsCommandException {
        return plotService.getPlotByLocation(player.getLocation()).orElseThrow(() ->
                new TownsCommandException("No plot found at your position"));
    }

    private Optional<Plot> getPlotAtPlayerOptional(Player player) {
        return plotService.getPlotByLocation(player.getLocation());
    }

    public boolean hasPlotAccess(Player player, Plot plot, WorldPermission permission) {
        Resident resPlayer = residentService.getOrCreate(player);

        if (plot.getOwner() != null) {
            Resident plotOwner = plot.getOwner();
            if ((plotOwner == resPlayer) || plotOwner.getFriends().contains(resPlayer)) {
                return true;
            }
        }
        return player.hasPermission(permission.getId());
    }

    public void plotAccessCheck(Cancellable event, Player player, WorldPermission permission, Location<World> location, boolean messageUser) {
        plotService.getPlotByLocation(location).ifPresent(plot -> {
            if (!hasPlotAccess(player, plot, permission)) {
                if (messageUser) {
                    townsMsg.error(player, "You do not have permission to do that!");
                }
                event.setCancelled(true);
            }
        });
    }

    private int getRight(Vector2i pointA, Vector2i pointB) {
        return pointA.getX() - pointB.getX();
    }

    private int getDown(Vector2i pointA, Vector2i pointB) {
        return pointB.getY() - pointA.getY();
    }

    public void showPlotBorders(Player player, boolean view) throws TownsCommandException {
        Logger logger = AtherysTowns.getInstance().getLogger();
        logger.info("showPlotBorders executed");

        residentService.getOrCreate(player).setIsViewingTownBorders(view);

        Plot playerPlot = getPlotAtPlayer(player);
        Vector2i northEastCorner = playerPlot.getNorthEastCorner();
        Vector2i southWestCorner = playerPlot.getSouthWestCorner();
        int right = getRight(northEastCorner, southWestCorner);
        int down = getDown(northEastCorner, southWestCorner);
        ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).option(ParticleOptions.COLOR, Color.BLUE)
                .quantity(5).offset(new Vector3d(0,2,0)).build();
        Vector3d particleLocationNE = new Vector3d(northEastCorner.getX() + .5, player.getPosition().getFloorY() + 2, northEastCorner.getY() + .5);
        Vector3d particleLocationSW = new Vector3d(southWestCorner.getX() + .5, player.getPosition().getFloorY() + 2, southWestCorner.getY() + .5);

        Task.Builder taskBuilder = Task.builder();

        taskBuilder.execute(task -> {
            for (int i = 0; i < down; i++) {
                player.spawnParticles(effect, particleLocationNE.add(0,0,i));
                player.spawnParticles(effect, particleLocationSW.add(i,0,0));
            }
            for (int i = 0; i < right; i++) {
                player.spawnParticles(effect, particleLocationNE.sub(i,0,0));
                player.spawnParticles(effect, particleLocationSW.sub(0,0,i));
            }

            if(!residentService.getOrCreate(player).getIsViewingTownBorders()) { logger.info("Cancelling Task..."); task.cancel(); }
        }).intervalTicks(10).submit(AtherysTowns.getInstance());

    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<Plot> plot = plotService.getPlotByLocation(to.getLocation());
        if (!plot.isPresent()) return;

        Optional<Plot> plotFrom = plotService.getPlotByLocation(from.getLocation());
        if (plotFrom.isPresent()) return;

        player.sendTitle(Title.builder().stay(20).title(Text.of(plot.get().getTown().getName())).build());
    }
}
