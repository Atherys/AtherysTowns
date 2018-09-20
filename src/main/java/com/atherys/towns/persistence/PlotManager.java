package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Town;
import com.flowpowered.math.vector.Vector2d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class PlotManager extends MorphiaDatabaseManager<Plot> {

    private static PlotManager instance = new PlotManager();

    private PlotManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Plot.class);
    }

    public Optional<Plot> createPlot(Town town, Vector2d A, Vector2d B) {
        Plot result = null;

        if (A.compareTo(B) > 0) result = new Plot(town, A, B);

        if (A.compareTo(B) < 0) result = new Plot(town, B, A);

        return Optional.ofNullable(result);
    }

    public boolean plotContains(Plot plot, Location<World> location) {
        boolean sameWorld = plot.getWorld().equals(location.getExtent());
        boolean withinX = plot.getMin().getX() >= location.getPosition().getX() && plot.getMax().getX() <= location.getPosition().getX();
        boolean withinZ = plot.getMin().getY() >= location.getPosition().getZ() && plot.getMax().getY() <= location.getPosition().getZ();
        return sameWorld && withinX && withinZ;
    }

    public boolean plotsIntersect(Plot A, Plot B) {
        return A.getMin().getX() < B.getMax().getX() &&
                A.getMin().getY() < B.getMax().getY() &&
                A.getMax().getX() > B.getMin().getX() &&
                A.getMax().getY() > B.getMin().getY();
    }

    public static PlotManager getInstance() {
        return instance;
    }
}
