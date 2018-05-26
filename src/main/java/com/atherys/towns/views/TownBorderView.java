package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.api.plot.IPlot;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.town.Town;
import com.flowpowered.math.vector.Vector3d;
import java.util.LinkedList;
import java.util.List;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;

public class TownBorderView implements View<Town> {

    private final ITown town;

    public TownBorderView(ITown town) {
        this.town = town;
    }

    @Override
    public void show(Player player) {
        showBorders(player);
    }

    private void showBorders(Player p) {
        List<LineSegment2D> borderedEdges = new LinkedList<>();
        for (IPlot plot : town.getPlots()) {
            for (LineSegment2D edge : plot.getDefinition().edges()) {
                if (!doesEdgeAlmostEqualAnyOther(edge, borderedEdges)) {
                    for (int i = 0; i <= edge.length(); i += 1) {
                        Point2D twoD = interpolationByDistance(edge, i);

                        Vector3d loc = new Vector3d(
                            twoD.x(),
                            p.getLocation().getExtent().getHighestYAt(
                                (int) twoD.x(),
                                (int) twoD.y()
                            ),
                            twoD.y()
                        );

                        p.spawnParticles(ParticleEffect.builder()
                            .velocity(Vector3d.from(0, 0.08, 0))
                            .type(ParticleTypes.BARRIER)
                            .quantity(1)
                            .build(), loc);
                    }
                    borderedEdges.add(edge);
                }
            }
        }
    }

    private static Point2D interpolationByDistance(LineSegment2D l, double d) {
        if (d == 0) {
            return l.firstPoint();
        }
        if (d == l.length()) {
            return l.lastPoint();
        }
        double len = l.length();
        double ratio = d / len;
        double x = ratio * l.lastPoint().x() + (1.0 - ratio) * l.firstPoint().x();
        double y = ratio * l.lastPoint().y() + (1.0 - ratio) * l.firstPoint().y();
        return new Point2D(x, y);
    }

    private static boolean doesEdgeAlmostEqualAnyOther(LineSegment2D edge,
        List<LineSegment2D> list) {
        for (LineSegment2D e : list) {
            if (edge.almostEquals(e, 1.0d)) {
                return true;
            }
        }
        return false;
    }
}
