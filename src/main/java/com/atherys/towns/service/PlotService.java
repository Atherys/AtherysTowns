package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.persistence.NationPlotRepository;
import com.atherys.towns.persistence.TownPlotRepository;
import com.atherys.towns.model.PlotSelection;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.Plot;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class PlotService {

    public static final Text DEFAULT_TOWN_PLOT_NAME = Text.of("None");

    @Inject
    TownsConfig config;

    @Inject
    TownPlotRepository townPlotRepository;

    @Inject
    NationPlotRepository nationPlotRepository;

    @Inject
    ResidentService residentService;

    PlotService() {
    }

    public boolean isLocationWithinPlot(Location<World> location, Plot plot) {
        return MathUtils.pointInRectangle(location.getPosition(), plot);
    }

    public static Set<Vector2i> getChunksOverlappedByPlot(Plot plot) {
        Vector2i southWestCorner = plot.getSouthWestCorner();
        Vector2i northEastCorner = plot.getNorthEastCorner();

        Set<Vector2i> chunkCoordinates = new HashSet<>();

        // Calculate min corner and max corner chunks
        Vector2i southWestChunk = Vector2i.from(southWestCorner.getX() >> 4, southWestCorner.getY() >> 4); // South West Corner Chunk
        Vector2i northEastChunk = Vector2i.from(northEastCorner.getX() >> 4, northEastCorner.getY() >> 4); // North East Corner Chunk

        for (int x = southWestChunk.getX(); x <= northEastChunk.getX(); x++) {
            for (int y = northEastChunk.getY(); y <= southWestChunk.getY(); y++) {
                chunkCoordinates.add(Vector2i.from(x, y));
            }
        }
        return chunkCoordinates;
    }

    public TownPlot createTownPlotFromSelection(PlotSelection selection) {
        TownPlot plot = new TownPlot();
        MathUtils.populateRectangleFromTwoCorners(plot, selection.getPointAVector(), selection.getPointBVector());
        plot.setName(DEFAULT_TOWN_PLOT_NAME);
        return plot;
    }

    public boolean townPlotIntersectAnyOthers(TownPlot plot) {
        for (Vector2i chunkCoordinate : getChunksOverlappedByPlot(plot)) {
            for (TownPlot other : townPlotRepository.getPlotsIntersectingChunk(chunkCoordinate)) {
                if (MathUtils.overlaps(plot, other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<TownPlot> getTownPlotByLocation(Location<World> location) {
        for (TownPlot plot : townPlotRepository.getPlotsIntersectingChunk(location.getChunkPosition())) {
            if (isLocationWithinPlot(location, plot)) {
                return Optional.of(plot);
            }
        }

        return Optional.empty();
    }

    public void setTownPlotName(TownPlot plot, Text newName) {
        plot.setName(newName);
        townPlotRepository.saveOne(plot);
    }

    public void setTownPlotOwner(TownPlot plot, Resident owner) {
        plot.setOwner(owner);
        plot.setPermissions(getDefaultPlotPermissions());

        townPlotRepository.saveOne(plot);
    }

    public boolean townPlotBordersTown(Town town, TownPlot plot) {
        for (TownPlot townPlot : town.getPlots()) {
            if (MathUtils.borders(plot, townPlot)) {
                return true;
            }
        }
        return false;
    }

    public boolean townPlotIntersectsTown(Town town, TownPlot plot) {
        for (TownPlot townPlot : town.getPlots()) {
            if (!MathUtils.overlaps(townPlot, plot)) {
                return true;
            }
        }

        return false;
    }

    public NationPlot createNationPlotFromSelection(PlotSelection selection) {
        NationPlot plot = new NationPlot();
        MathUtils.populateRectangleFromTwoCorners(plot, selection.getPointAVector(), selection.getPointBVector());
        return plot;
    }

    public Set<NationPlot> getNationPlotsByLocation(Location<World> location) {
        return nationPlotRepository.getAll().stream().filter(plot -> isLocationWithinPlot(location, plot))
                .collect(Collectors.toSet());
    }

    public Optional<NationPlot> getNationPlotsByTownPlot(TownPlot tPlot) {
        return nationPlotRepository.getAll().stream().filter(plot ->
                MathUtils.overlaps(tPlot, plot)).findFirst();
    }

    public boolean permissionAlreadyExistsInContext(TownsPermissionContext context, TownPlot plot, WorldPermission permission) {
        return plot.getPermissions().stream().anyMatch(p -> p.getContext().equals(context) && p.getWorldPermission().equals(permission));
    }

    public void addPlotPermission(TownPlot plot, TownsPermissionContext type, WorldPermission permission) {
        TownPlotPermission townPlotPermission = new TownPlotPermission();
        townPlotPermission.setContext(type);
        townPlotPermission.setWorldPermission(permission);

        plot.getPermissions().add(townPlotPermission);

        townPlotRepository.saveOne(plot);
    }

    public void removePlotPermission(TownPlot plot, TownsPermissionContext type, WorldPermission permission) {
        plot.getPermissions().removeIf(p -> p.getWorldPermission().equals(permission) && p.getContext().equals(type));

        townPlotRepository.saveOne(plot);
    }

    public Set<TownPlotPermission> getDefaultPlotPermissions() {
        Set<TownPlotPermission> defaultTownPlotPermissions = new HashSet<>();

        config.TOWN.DEFAULT_PLOT_PERMISSIONS.forEach(config -> {
            TownPlotPermission townPlotPermission = new TownPlotPermission();
            townPlotPermission.setContext(config.getContext());
            townPlotPermission.setWorldPermission(config.getWorldPermission());
            defaultTownPlotPermissions.add(townPlotPermission);
        });

        return defaultTownPlotPermissions;
    }

    public Optional<TownPlot> getClosestTownPlot(Plot plot) {
        int centerX = (plot.getNorthEastCorner().getX() + plot.getSouthWestCorner().getX()) / 2;
        int centerZ = (plot.getNorthEastCorner().getY() + plot.getSouthWestCorner().getY()) / 2;

        TownPlot closest = null;
        double distance = 0;
        for (TownPlot p : townPlotRepository.getAll()) {
            if (p.equals(plot)) {
                continue;
            }

            double newDistance = MathUtils.getDistanceToPlotSquared(Vector2i.from(centerX, centerZ), p);
            if (closest == null || newDistance < distance) {
                closest = p;
                distance = newDistance;
            }
        }

        return Optional.ofNullable(closest);
    }
}
