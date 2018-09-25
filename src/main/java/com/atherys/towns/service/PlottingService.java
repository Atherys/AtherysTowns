package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.flowpowered.math.vector.Vector2d;
import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class PlottingService {
    private Map<UUID, MutablePair<Vector2d, Vector2d>> players = new HashMap<>();

    private static PlottingService instance = new PlottingService();

    public static PlottingService getInstance() {
        return instance;
    }

    private PlottingService() {
    }

    public void startPlotting(Player player) {
        players.put(player.getUniqueId(), new MutablePair<>());
    }

    public boolean isPlotting(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public void progress(Player player, Vector2d point) {
        MutablePair<Vector2d, Vector2d> points = players.get(player.getUniqueId());
        if (points == null) {
             players.put(player.getUniqueId(), new MutablePair<>());
             points = players.get(player.getUniqueId());
        }

        if ( points.getLeft() == null ) {
            points.setLeft(point);
        } else {
            points.setRight(point);
            AtherysTowns.getTownManager().createTownFromSelection(
                    points.getLeft(),
                    points.getRight(),
                    AtherysTowns.getResidentManager().getOrCreate(player.getUniqueId()),
                    player.getWorld()
            );
            players.remove(player.getUniqueId());
        }
    }
}
