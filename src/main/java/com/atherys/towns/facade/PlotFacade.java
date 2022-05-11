package com.atherys.towns.facade;

import com.atherys.core.utils.TextUtils;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.TownsPermissionContexts;
import com.atherys.towns.api.permission.town.TownPermissions;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownsPermissionService;
import com.atherys.towns.util.MathUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.Text.NEW_LINE;
import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class PlotFacade {

    @Inject
    TownsConfig config;

    @Inject
    PlotService plotService;

    @Inject
    PermissionFacade permissionFacade;

    @Inject
    TownsMessagingFacade townsMsg;

    @Inject
    ResidentService residentService;

    @Inject
    TownsPermissionService townsPermissionService;

    @Inject
    TownFacade townFacade;

    PlotFacade() {}

    public void renameTownPlotAtPlayerLocation(Player player, Text newName) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);

        if (permissionFacade.isPermitted(player, TownPermissions.RENAME_PLOT) ||
                residentService.getOrCreate(player).equals(plot.getOwner())) {

            plotService.setTownPlotName(plot, newName);
            townsMsg.info(player, "Plot renamed.");
        } else {
            throw new TownsCommandException("You are not permitted to rename this plot.");
        }
    }

    public void sendInfoOnPlotAtPlayerLocation(Player player) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);
        Resident plotOwner = (plot.getOwner() != null) ? plot.getOwner() : new Resident();
        String ownerName = (plotOwner.getName() != null) ? plotOwner.getName() : "None";
        Text.Builder plotText = Text.builder();

        plotText.append(townsMsg.createTownsHeader(plot.getName().toPlain()));

        plotText.append(Text.of(DARK_GREEN, "Town: ", townFacade.renderTown(plot.getTown()), NEW_LINE));

        plotText
                .append(Text.of(DARK_GREEN, "Owner: ", GOLD, ownerName, NEW_LINE))
                .append(Text.of(DARK_GREEN, "Size: ", GOLD, MathUtils.getArea(plot), NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point A: ", GOLD, "x: ", plot.getSouthWestCorner().getX(), ", z: ", plot.getSouthWestCorner().getY(), NEW_LINE))
                .append(Text.of(DARK_GREEN, "Point B: ", GOLD, "x: ", plot.getNorthEastCorner().getX(), ", z: ", plot.getNorthEastCorner().getY()));

        player.sendMessage(plotText.build());
    }

    public void grantPlotAtPlayerLocation(Player player, User target) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);

        plotService.setTownPlotOwner(plot, residentService.getOrCreate(target));

        townsMsg.info(player, "Granted the plot ", GOLD, plot.getName(), DARK_GREEN, " to ", GOLD, target.getName(), DARK_GREEN, ".");
    }

    public void revokePlotAtPlayerLocation(Player player) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);

        plotService.setTownPlotOwner(plot, null);
        townsMsg.info(player, "Revoked ownership of plot ", GOLD, plot.getName(), DARK_GREEN, ".");
    }

    public TownPlot getPlotAtPlayer(Player player) throws TownsCommandException {
        return plotService.getTownPlotByLocation(player.getLocation()).orElseThrow(() ->
                new TownsCommandException("You are not standing in a plot."));
    }

    public boolean checkHostileSpawningAtLocation(Location<World> location) {
        return plotService.getTownPlotByLocation(location).map(plot -> plot.getTown().isMobs()).orElse(false);
    }

    public Set<TownsPermissionContext> getRelevantResidentContexts(TownPlot plot, Resident resident) {
        Set<TownsPermissionContext> contexts = new HashSet<>();
        contexts.add(TownsPermissionContexts.ALL);

        Resident owner = plot.getOwner();

        if (owner != null && owner.getFriends().contains(resident)) {
            contexts.add(TownsPermissionContexts.FRIEND);
        }

        Town plotTown = plot.getTown();
        Town resTown = resident.getTown();

        if (resTown == null) {
            contexts.add(TownsPermissionContexts.NEUTRAL);
        } else {
            // If resident town is the same town the plot is in, apply town permissions
            if (resTown == plotTown) {
                contexts.add(TownsPermissionContexts.TOWN);
            }

            Nation plotNation = plotTown.getNation();
            Nation resNation = resTown.getNation();

            if (plotNation == null || resNation == null) {
                contexts.add(TownsPermissionContexts.NEUTRAL);
            } else {

                // If resident nation is the plot nation, apply ally permissions
                // If resident nation is an ally to plot nation, apply ally permissions
                if (resNation == plotNation || plotNation.getAllies().contains(resNation)) {
                    contexts.add(TownsPermissionContexts.ALLY);
                }

                // If resident nation is an enemy to plot nation, apply enemy permissions
                if (plotNation.getEnemies().contains(resNation)) {
                    contexts.add(TownsPermissionContexts.ENEMY);
                }
            }
        }
        return contexts;
    }

    public boolean hasPlotAccess(User player, TownPlot plot, WorldPermission permission) {
        Resident resPlayer = residentService.getOrCreate(player);
        Resident plotOwner = plot.getOwner();

        if (plotOwner == resPlayer) {
            return true;
        }

        if (plot.getRentInfo().isPresent()) {
            Resident renter = plot.getRentInfo().get().getRenter();
            if (renter != null) {
                return resPlayer == renter || renter.getFriends().contains(resPlayer);
            }
        }

        Set<TownsPermissionContext> contexts = getRelevantResidentContexts(plot, resPlayer);

        if (plotOwner == null) {
            // Get raw permission value from player
            Tristate permissionValue = player.getPermissionValue(townsPermissionService.getContextsForTown(plot.getTown()), permission.getId());

            if (permissionValue.equals(Tristate.UNDEFINED)) {
                return config.TOWN.DEFAULT_PLOT_PERMISSIONS.stream().anyMatch(p ->
                        p.getWorldPermission().equals(permission) && contexts.contains(p.getContext())
                );
            }

            return permissionValue.asBoolean();
        }

        return plot.getPermissions().stream().anyMatch(p ->
                permission.equals(p.getWorldPermission()) && contexts.contains(p.getContext())
        );
    }

    public void plotAccessCheck(Cancellable event, User player, WorldPermission permission, Location<World> location, boolean messageUser) {
        plotService.getTownPlotByLocation(location).ifPresent(plot -> {
            if (!hasPlotAccess(player, plot, permission)) {
                if (messageUser && player instanceof Player) {
                    townsMsg.error((MessageReceiver) player, "You do not have permission to do that!");
                }
                event.setCancelled(true);
            }
        });
    }

    public void onPlayerMove(Transform<World> from, Transform<World> to, Player player) {
        Optional<TownPlot> plotTo = plotService.getTownPlotByLocation(to.getLocation());
        Optional<TownPlot> plotFrom = plotService.getTownPlotByLocation(from.getLocation());

        boolean plotToHasRent = plotTo.map(p -> p.getRentInfo().map(rent -> rent.getRenter() == null).orElse(false))
                .orElse(false);

        if (plotToHasRent && (!plotFrom.isPresent() || plotTo.get() != plotFrom.get())) {
            RentInfo rentInfo = plotTo.get().getRentInfo().get();
            Text price = config.DEFAULT_CURRENCY.format(rentInfo.getPrice());
            Text duration = TextUtils.formatDuration(rentInfo.getPeriod().toMillis());

            Title title = Title.of(
                    Text.of(GOLD, plotTo.get().getName()),
                    Text.of(DARK_GREEN, "Rent for ", GOLD, price, DARK_GREEN, " per ", GOLD, duration)
            );


            player.sendTitle(title);
            return;
        }

        if (plotTo.isPresent() && !plotFrom.isPresent()) {
            TownPlot plot = plotTo.get();

            Title title = Title.of(Text.of(plot.getTown().getColor(), plot.getTown().getName()), Text.EMPTY);

            player.sendTitle(title);
        }

        if (!plotTo.isPresent() && plotFrom.isPresent()) {
            player.sendTitle(Title.of(Text.of(GREEN, "Wilderness"), Text.EMPTY));
        }
    }

    private void verifyPlotOwnership(TownPlot plot, Player player) throws TownsCommandException {
        if (plot.getOwner() == null) {
            throw new TownsCommandException("This plot does not have an owner!");
        }

        Resident resident = residentService.getOrCreate(player);

        if (!plot.getOwner().equals(resident)) {
            throw new TownsCommandException("You are not the owner of this plot!");
        }
    }

    public void addPlotPermission(Player player, TownsPermissionContext type, WorldPermission permission) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);
        verifyPlotOwnership(plot, player);

        if (plotService.permissionAlreadyExistsInContext(type, plot, permission)) {
            throw new TownsCommandException("You have already added this permission for this group!");
        }

        plotService.addPlotPermission(plot, type, permission);
        townsMsg.info(player, "Added the ", GOLD, permission.getName(), DARK_GREEN, " permission to the ",
                GOLD, type.toString(), DARK_GREEN, " group.");
    }

    public void removePlotPermission(Player player, TownsPermissionContext type, WorldPermission permission) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);
        verifyPlotOwnership(plot, player);

        if (!plotService.permissionAlreadyExistsInContext(type, plot, permission)) {
            throw new TownsCommandException("This permission does not exist within the specified group!");
        }

        plotService.removePlotPermission(plot, type, permission);
        townsMsg.info(player, "Removed the ", GOLD, permission.getName(), DARK_GREEN, " permission from the ",
                GOLD, type.toString(), DARK_GREEN, " group.");
    }

    public void sendPlotPermissions(Player player) {
        Text.Builder plotPermsText = Text.builder();

        plotPermsText
                .append(townsMsg.createTownsHeader("Plot Permissions"));

        permissionFacade.WORLD_PERMISSIONS.forEach((s, worldPermission) ->
                plotPermsText.append(Text.of(DARK_GREEN, worldPermission.getName(), ": ", GOLD, s, NEW_LINE)));

        player.sendMessage(plotPermsText.build());
    }

    public void sendCurrentPlotPermissions(Player player) throws TownsCommandException {
        TownPlot plot = getPlotAtPlayer(player);
        verifyPlotOwnership(plot, player);
        Text.Builder plotPermsText = Text.builder();

        plotPermsText.append(townsMsg.createTownsHeader("Plot Permissions"));

        permissionFacade.WORLD_PERMISSIONS.forEach((s, worldPermission) -> {
            Set<TownPlotPermission> permissions = plot.getPermissions();
            Set<String> groups = new HashSet<>();

            if (permissions != null) {
                groups = permissions.stream()
                        .filter(p -> p.getWorldPermission().equals(worldPermission))
                        .map(p -> p.getContext().getName())
                        .collect(Collectors.toSet());
            }

            if (groups.size() > 0) {
                plotPermsText.append(Text.of(DARK_GREEN, worldPermission.getName(), ": ", GOLD, String.join(", ", groups), NEW_LINE));
            }
        });

        player.sendMessage(plotPermsText.build());
    }
}
