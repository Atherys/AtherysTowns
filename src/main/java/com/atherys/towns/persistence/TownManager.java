package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.flowpowered.math.vector.Vector2d;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class TownManager extends MorphiaDatabaseManager<Town> {

    public TownManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Town.class);
    }

    public void registerTown(Town town) {
        this.getCache().put(town.getUniqueId(), town);
    }

    public Optional<Town> createTownFromSelection(Vector2d A, Vector2d B, Resident mayor, World world) {
        Town town = new Town(mayor, world);

        Optional<Plot> plot = AtherysTowns.getPlotManager().createPlot(town, A, B);

        if (!plot.isPresent()) return Optional.empty();
        else {

            town.addPlot(plot.get());
            town.setName(AtherysTowns.getConfig().DEFAULT_TOWN_NAME);
            town.setColor(AtherysTowns.getConfig().DEFAULT_TOWN_COLOR);
            town.setDescription(AtherysTowns.getConfig().DEFAULT_TOWN_DESCRIPTION);
            town.setMaxArea(AtherysTowns.getConfig().DEFAULT_TOWN_SIZE);
            town.setMotd(AtherysTowns.getConfig().DEFAULT_TOWN_MOTD);

            return Optional.of(town);
        }
    }
}
