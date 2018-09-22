package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.flowpowered.math.vector.Vector2d;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class PlottingService {
    private Map<UUID, List<Vector2d>> players = new HashMap<>();

    private static PlottingService instance = new PlottingService();

    public static PlottingService getInstance() {
        return instance;
    }

    private PlottingService() {
    }

    public void startPlotting(Player player) {
        players.put(player.getUniqueId(), new ArrayList<>());
    }

    public boolean isPlotting(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public void progress(Player player, Vector2d point) {
        List<Vector2d> points = players.get(player.getUniqueId());
        if (points == null) {
             players.put(player.getUniqueId(), new ArrayList<>());
             points = players.get(player.getUniqueId());
        }

        switch (points.size()) {
            case 0:
                points.add(point);
            case 1:
                points.add(point);
                AtherysTowns.getTownManager().createTownFromSelection(
                        points.get(0),
                        points.get(1),
                        AtherysTowns.getResidentManager().getOrCreate(player.getUniqueId()));
                players.remove(player.getUniqueId());
        }
    }
}
