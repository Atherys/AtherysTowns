package com.atherys.towns.service;

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

    public boolean permissionAlreadyExistsInContext(AllianceType type, TownPlot plot, WorldPermission permission) {
        switch (type) {
            case ALLY:
                return plot.getAllyPermissions().contains(permission);
            case ENEMY:
                return plot.getEnemyPermissions().contains(permission);
            case FRIEND:
                return plot.getFriendPermissions().contains(permission);
            case TOWN:
                return plot.getTownPermissions().contains(permission);
            case NEUTRAL:
                return plot.getNeutralPermissions().contains(permission);
        }
        return false;
    }

    public void addPlotPermission(TownPlot plot, AllianceType type, WorldPermission permission) {
        switch (type) {
            case ALLY:
                plot.getAllyPermissions().add(permission);
                break;
            case ENEMY:
                plot.getEnemyPermissions().add(permission);
                break;
            case FRIEND:
                plot.getFriendPermissions().add(permission);
                break;
            case TOWN:
                plot.getTownPermissions().add(permission);
                break;
            case NEUTRAL:
                plot.getNeutralPermissions().add(permission);
                break;
        }

        townPlotRepository.saveOne(plot);
    }

    public void removePlotPermission(TownPlot plot, AllianceType type, WorldPermission permission) {
        switch (type) {
            case ALLY:
                plot.getAllyPermissions().remove(permission);
                break;
            case ENEMY:
                plot.getEnemyPermissions().remove(permission);
                break;
            case FRIEND:
                plot.getFriendPermissions().remove(permission);
                break;
            case TOWN:
                plot.getTownPermissions().remove(permission);
                break;
            case NEUTRAL:
                plot.getNeutralPermissions().remove(permission);
                break;
        }

        townPlotRepository.saveOne(plot);
    }

    public enum AllianceType {
        ALLY,
        ENEMY,
        FRIEND,
        TOWN,
        NEUTRAL
    }
}
