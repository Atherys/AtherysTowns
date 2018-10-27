package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.Plot;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.atherys.towns.model.permission.ResidentAction;
import com.atherys.towns.model.permission.ResidentRank;
import com.atherys.towns.model.permission.ResidentRights;
import com.flowpowered.math.vector.Vector2d;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

            Set<ResidentAction> rights = new HashSet<>();
            rights.add(ResidentRights.GRANT_PLOT_PERMISSIONS);
            rights.add(ResidentRights.INVITE_MEMBER);
            rights.add(ResidentRights.DEMOTE_RESIDENT);
            rights.add(ResidentRights.PROMOTE_RESIDENT);

            AtherysTowns.getRankManager().createRank(town, ResidentRank.OWNER,"major", rights);
            return Optional.of(town);
        }
    }

    public void addTownToNation(Town origin, Nation nation) {
        origin.setNation(nation);
        update(origin);
    }
}
