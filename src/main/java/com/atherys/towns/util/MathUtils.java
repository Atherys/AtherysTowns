package com.atherys.towns.util;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import java.lang.Math;

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
        return Math.abs(pointA.getX() - pointB.getX());
    }

    public static int getZLength(Vector2i pointA, Vector2i pointB) {
        return Math.abs(pointB.getY() - pointA.getY());
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

    public static boolean overlaps(Rectangle a, Rectangle b) {
        // Rectangles are considered overlapping when any of the interior points are shared
        return !(b.minY() > a.maxY() || b.minX() > a.maxX() || b.maxY() < a.minY() || b.maxX() < a.minX());
    }

    public static boolean contains(Rectangle a, Rectangle b) {
        // a contains b iff no points of b lie in the exterior of a, and at least one point of the interior of b lies in the interior of a
        return (a.maxY() >= b.maxY() && a.minY() >= b.minY() && a.maxX() >= b.maxX() && a.minX() >= b.minX());
    }

    public static boolean equals(Rectangle a, Rectangle b) {
        return a.getTopLeftCorner() == b.getTopLeftCorner() &&
               a.getBottomRightCorner() == b.getBottomRightCorner();
    }

    public static boolean touches(Rectangle a, Rectangle b) {
        // a touches b: they have at least one point in common, but their interiors do not intersect.

        // If Top or bottom is shared and X overlaps
        if ((a.minY() == b.maxY() || a.maxY() == b.minY()) &&
                a.minX() <= b.maxX() && b.minX() <= a.maxX()) {
            return true;
        }

        // If Left or Right is shared and Y overlaps
        if ((a.minX() == b.maxX() || a.maxX() == b.minX()) &&
                a.minY() <= b.maxY() && b.minY() <= a.maxY()) {
            return true;
        }

        return false;
    }

    public static boolean borders(Rectangle a, Rectangle b) {
        // a borders b if they have at least 2 points in common, but their interiors do not intersect.
        // excludes touching corners

        // If Top or bottom is shared and X overlaps
        if ((a.minY() == b.maxY() || a.maxY() == b.minY()) &&
                a.minX() < b.maxX() && b.minX() < a.maxX()) {
            return true;
        }

        // If Left or Right is shared and Y overlaps
        if ((a.minX() == b.maxX() || a.maxX() == b.minX()) &&
                a.minY() < b.maxY() && b.minY() < a.maxY()) {
            return true;
        }

        return false;
    }

    public static Vector2i getPlotSize(Vector2i NECorner, Vector2i SWCorner) {
        int sizeX = Math.abs(NECorner.getX() - SWCorner.getX());
        int sizeY = Math.abs(NECorner.getY() - SWCorner.getY());
        return new Vector2i(sizeX, sizeY);
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

//    /**
//     * Subtracts RectB from RectA returning at most 4 new rectangles
//     *
//     *   ****************      ****************      ****************
//     *   |      RA      |      |              |      |      1       |
//     *   |    ******    |      |    ******    |      |**************|
//     *   |    | RB |    |  ->  |    |Hole|    |  ->  |  2 |Hole| 3  |
//     *   |    ******    |      |    ******    |      |**************|
//     *   |              |      |              |      |      4       |
//     *   ****************      ****************      ****************
//     *
//     * @param rectASouthWest Rectangle A top left corner
//     * @param rectANorthEast Rectangle A bottom right corner
//     * @param rectBSouthWest Rectangle B top left corner
//     * @param rectBNorthEast Rectangle B bottom right corner
//     * @return A List of rectangles that make up the remainder of RectA
//     */
//    public static List<Rectangle> subtractRectangles(Vector2i rectASouthWest, Vector2i rectANorthEast,
//                                                     Vector2i rectBSouthWest, Vector2i rectBNorthEast) {
//        // Handle edge cases
//        // Same rectangle
//        if (rectASouthWest == rectBSouthWest && rectANorthEast == rectBNorthEast) {
//            return new ArrayList<>();
//        }
//        // RectB Contains RectA
//        if (contains(rectBSouthWest, rectBNorthEast, rectASouthWest, rectANorthEast)) {
//            return new ArrayList<>();
//        }
//        // No intersect
//        if (!overlaps(rectASouthWest, rectANorthEast, rectBSouthWest, rectBNorthEast)) {
//            return new ArrayList<Rectangle>() {{
//                add(new Rectangle(rectASouthWest, rectANorthEast));
//            }};
//        }
//
//        ArrayList<Rectangle> results = new ArrayList<>();
//
//        // Get top rectangle
//        if (rectBSouthWest.getY() < rectASouthWest.getY()) {
//            results.add(new Rectangle(rectASouthWest, Vector2i.from(rectANorthEast.getX(), rectBSouthWest.getY())));
//        }
//
//        // Get left rectangle
//        if (rectBSouthWest.getX() > rectASouthWest.getX()) {
//            results.add(new Rectangle(Vector2i.from(rectASouthWest.getX(), Math.min(rectASouthWest.getY(), rectBSouthWest.getY())),
//                        Vector2i.from(rectBSouthWest.getX(), Math.max(rectANorthEast.getY(), rectBNorthEast.getY()))));
//        }
//
//        // Get right rectangle
//        if (rectBNorthEast.getX() < rectANorthEast.getX()) {
//            results.add(new Rectangle(Vector2i.from(rectBNorthEast.getX(), Math.min(rectASouthWest.getY(), rectBSouthWest.getY())),
//                        Vector2i.from(rectANorthEast.getX(), Math.max(rectANorthEast.getY(), rectBNorthEast.getY()))));
//        }
//
//        // Get bottom rectangle
//        if (rectANorthEast.getY() < rectBNorthEast.getY()) {
//            results.add(new Rectangle(Vector2i.from(rectASouthWest.getX(), rectBNorthEast.getY()) , rectANorthEast));
//        }
//
//        return results;
//    }
//
//    /**
//     * Checks to see if a rectangle is fully contained within another set of rectangles
//     * @param target The rectangle to check
//     * @param set The set of rectangles
//     * @return True if the target is fully within the area covered by the set, otherwise false
//     */
//    public static boolean rectangleContainedInSet(Rectangle target, Set<Rectangle> set) {
//        List<Rectangle> remaining = new ArrayList<Rectangle>() {{
//            add(target);
//        }};
//
//        for (Rectangle rect : set) {
//            ListIterator<Rectangle> iterator = remaining.listIterator();
//            while (iterator.hasNext()) {
//                Rectangle tar = iterator.next();
//                iterator.remove();
//
//                subtractRectangles(tar.getSouthWest(), tar.getNorthEast(), rect.getSouthWest(), rect.getNorthEast())
//                        .forEach(iterator::add);
//            }
//        }
//
//        remaining.forEach(System.out::print);
//
//        return remaining.isEmpty();
//    }
}
