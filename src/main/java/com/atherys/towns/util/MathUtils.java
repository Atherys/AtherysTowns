package com.atherys.towns.util;

import com.atherys.towns.model.entity.Plot;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class MathUtils {

    public static Vector2i vec3iToVec2i(Vector3i vector3i) {
        return Vector2i.from(vector3i.getX(), vector3i.getZ());
    }

    public static Vector2i vec3dToVec2i(Vector3d vector3d) {
        return Vector2i.from(vector3d.getFloorX(), vector3d.getFloorZ());
    }

    public static double getDistanceBetweenPoints(Vector3d point1, Vector3d point2) {
        return Math.sqrt(Math.pow(point2.getFloorX() - point1.getFloorX(), 2) + Math.pow(point2.getFloorZ() - point1.getFloorZ(), 2));
    }

    public static double getDistanceBetweenPoints(Vector2i point1, Vector2i point2) {
        return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2));
    }

    public static int getXLength(Vector2i pointA, Vector2i pointB) {
        return pointA.getX() - pointB.getX();
    }

    public static int getZLength(Vector2i pointA, Vector2i pointB) {
        return pointB.getY() - pointA.getY();
    }

    public static boolean vectorFitsInRange(Vector3d vec, Vector3i lower, Vector3i upper) {
        return fitsInRange(vec.getX(), lower.getX(), upper.getX()) &&
                fitsInRange(vec.getY(), lower.getY(), upper.getY()) &&
                fitsInRange(vec.getZ(), lower.getZ(), upper.getZ());
    }

    public static boolean vectorFitsInRange(Vector3i vec, Vector3i lower, Vector3i upper) {
        return fitsInRange(vec.getX(), lower.getX(), upper.getX()) &&
                fitsInRange(vec.getY(), lower.getY(), upper.getY()) &&
                fitsInRange(vec.getZ(), lower.getZ(), upper.getZ());
    }

    public static boolean vectorFitsInRange(Vector2i vec, Vector2i lower, Vector2i upper) {
        return fitsInRange(vec.getX(), lower.getX(), upper.getX()) &&
                fitsInRange(vec.getY(), lower.getY(), upper.getY());
    }

    public static boolean vectorXZFitsInRange(Vector3d position, Vector2i lower, Vector2i upper) {
        return fitsInRange(position.getX(), lower.getX(), upper.getX()) &&
                fitsInRange(position.getZ(), upper.getY(), lower.getY());
    }

    public static boolean vectorXZFitsInRange(Vector3i position, Vector2i lower, Vector2i upper) {
        return fitsInRange(position.getX(), lower.getX(), upper.getX()) &&
                fitsInRange(position.getZ(), upper.getY(), lower.getY());
    }

    public static boolean fitsInRange(int number, int lower, int upper) {
        return number >= lower && number <= upper;
    }

    public static boolean fitsInRange(double number, int lower, int upper) {
        return number >= lower && number <= upper;
    }

    public static boolean overlaps(Vector2i rectASouthWest, Vector2i rectANorthEast, Vector2i rectBSouthWest, Vector2i rectBNorthEast) {
        return !(rectBNorthEast.getY() > rectASouthWest.getY() ||
                rectBSouthWest.getX() > rectANorthEast.getX() ||
                rectANorthEast.getY() > rectBSouthWest.getY() ||
                rectASouthWest.getX() > rectBNorthEast.getX());
    }

    public static boolean borders(Vector2i rectASouthWest, Vector2i rectANorthEast, Vector2i rectBSouthWest, Vector2i rectBNorthEast) {
        return overlaps(
                rectASouthWest.add(-1, 1), rectANorthEast.add(1, -1), rectBSouthWest, rectBNorthEast
        );
    }

    private static Set<Vector2i> getTownPlotPoints(Set<Plot> plots) {
        Set<Vector2i> plotPoints = new HashSet<>();
        plots.forEach(plot -> {
            plotPoints.add(plot.getNorthEastCorner());
            plotPoints.add(plot.getSouthWestCorner());
        });
        return plotPoints;
    }

    private static Vector2i getClosestPlot(Set<Plot> plots, Vector2i targetPoint) {
        Map<Double, Vector2i> pointDistances = new HashMap<>();
        getTownPlotPoints(plots).forEach(plotVector -> pointDistances.put(getDistanceBetweenPoints(targetPoint, plotVector), plotVector));
        return Collections.min(pointDistances.entrySet(), (entry1, entry2) -> (int) (entry1.getKey() - entry2.getKey())).getValue();
    }

    public static double getDistanceToPlot(Plot plot, Vector2i point) {
        int plotMinX = plot.getSouthWestCorner().getX();
        int plotMaxX = plot.getNorthEastCorner().getX();
        int plotMinY = plot.getSouthWestCorner().getY();
        int plotMaxY = plot.getNorthEastCorner().getY();
        int pointX = point.getX();
        int pointY = point.getY();

        if (pointX < plotMinX) {
            if (pointY <  plotMinY) return Math.hypot(plotMinX-pointX, plotMinY-pointY);
            if (pointY <= plotMaxY) return plotMinX - pointX;
            return Math.hypot(plotMinX-pointX, plotMaxY-pointY);
        } else if (pointX <= plotMaxX) {
            if (pointY <  plotMinY) return plotMinY - pointY;
            if (pointY <= plotMaxY) return 0;
            return pointY - plotMaxY;
        } else {
            if (pointY <  plotMinY) return Math.hypot(plotMaxX-pointX, plotMinY-pointY);
            if (pointY <= plotMaxY) return pointX - plotMaxX;
            return Math.hypot(plotMaxX-pointX, plotMaxY-pointY);
        }
    }

    public static double getSmallestDistanceToTown(Set<Plot> townPlots, Vector2i targetPoint) {
        Vector2i closestPlotPointVector = getClosestPlot(townPlots, targetPoint);
        Plot plot = townPlots.stream().filter(plot1 -> plot1.getSouthWestCorner().equals(closestPlotPointVector)
                || plot1.getNorthEastCorner().equals(closestPlotPointVector)).findFirst().get();

        return getDistanceToPlot(plot, targetPoint);
    }


}
