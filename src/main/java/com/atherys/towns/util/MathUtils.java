package com.atherys.towns.util;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class MathUtils {

    public static Vector2i vec3iToVec2i(Vector3i vector3i) {
        return Vector2i.from(vector3i.getX(), vector3i.getZ());
    }

    public static Vector2i vec3dToVec2i(Vector3d vector3d) {
        return Vector2i.from(vector3d.getFloorX(), vector3d.getFloorZ());
    }

    public static double getDistanceBetweenPointsSquared(Vector3d point1, Vector3d point2) {
        return Math.pow(point2.getFloorX() - point1.getFloorX(), 2) + Math.pow(point2.getFloorZ() - point1.getFloorZ(), 2);
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

    public static double getDistanceToPlotSquared(Vector2i point, Vector2i NECorner, Vector2i SWCorner) {
        int lengthX = MathUtils.getXLength(NECorner, SWCorner);
        int lengthZ = MathUtils.getZLength(NECorner, SWCorner);

        double centerX = (double) (NECorner.getX() + SWCorner.getX()) / 2;
        double centerZ = (double) (NECorner.getY() + SWCorner.getY()) / 2;

        double pointXLength = Math.max(Math.abs(point.getX() - centerX) - (double) lengthX / 2, 0);
        double pointYLength = Math.max(Math.abs(point.getY() - centerZ) - (double) lengthZ / 2, 0);

        return Math.pow(pointXLength, 2) + Math.pow(pointYLength, 2);
    }


}
