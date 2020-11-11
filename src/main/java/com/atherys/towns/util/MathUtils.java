package com.atherys.towns.util;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MathUtils {

    public static Vector2i vec3iToVec2i(Vector3i vector3i) {
        return Vector2i.from(vector3i.getX(), vector3i.getZ());
    }

    /**
     * Populate the SouthWest and NorthEast corners of a plot from a PlotSelection
     *
     * Minecraft's plane is aligned in an unexpected way, the SouthWest corner is the TopLeft corner
     * and the NorthEast corner is the BottomRight
     *
     *          South
     *           +z
     *            ^
     *            |
     * West -x ----- +x East
     *            |
     *           -z
     *          North
     *
     * The two points in a PlotSelection are not necessarily the corners we need, we may need to work out opposite corners
     *
     * Additionally, as we want the borders around a set of blocks, we need to Floor SW.x, Ceil SW.y, Ceil NE.x, Floor NE.y
     *
     * @param rect The Rectangle to populate
     * @param pA First point
     * @param pB Second point
     */
    public static void populateRectangleFromTwoCorners(Rectangle rect, Vector2d pA, Vector2d pB) {
        Vector2d pNE;
        Vector2d pSW;

        //             +z
        //     pB, SW  *
        //         +---+
        //         |   |
        //  -x  - +---+ - +X pA
        //         *    pA NE
        //         -z
        //
        if (pA.getX() > pB.getX() && pA.getY() < pB.getY()) {
            pNE = pA;
            pSW = pB;
            //             +z
            //     pA, SW  *
            //         +---+
            //         |   |
            //  -x  - +---+ - +X pA
            //         *    pB NE
            //         -z
            //
        } else if (pA.getX() < pB.getX() && pA.getY() > pB.getY()) {
            pNE = pB;
            pSW = pA;
            //         +z
            //         *     pA
            //         +---+
            //         |   |
            //  -x  - +---+ - +X pA
            //     pB      *
            //            -z
            //
        } else if (pA.getX() > pB.getX() && pA.getY() > pB.getY()) {
            pNE = Vector2d.from(pA.getX(), pB.getY());
            pSW = Vector2d.from(pB.getX(), pA.getY());
            //         +z
            //         *     pB
            //         +---+
            //         |   |
            //  -x  - +---+ - +X pA
            //     pA      *
            //            -z
            //
        } else if (pA.getX() < pB.getX() && pA.getY() < pB.getY()) {
            pNE = Vector2d.from(pB.getX(), pA.getY());
            pSW = Vector2d.from(pA.getX(), pB.getY());
        } else {
            throw new IllegalArgumentException("Could not resolve south-west and north-east plot points.");
        }

        rect.setTopLeftCorner(new Vector2i(pSW.getFloorX(), Math.ceil(pSW.getY())));
        rect.setBottomRightCorner(new Vector2i(Math.ceil(pNE.getX()), pNE.getFloorY()));
    }


    public static double getDistanceBetweenPointsSquared(Vector3d point1, Vector3d point2) {
        return Math.pow(point2.getFloorX() - point1.getFloorX(), 2) + Math.pow(point2.getFloorZ() - point1.getFloorZ(), 2);
    }

    public static boolean pointInRectangle(Vector3d point, Rectangle rect) {
        return pointInRectangle(point.toVector2(true), rect);
    }

    public static boolean pointInRectangle(Vector2d point, Rectangle rect) {
        return rect.maxY() >= point.getY() && point.getY() >= rect.minY() &&
                rect.maxX() >= point.getX() && point.getX() >= rect.minX();
    }

    public static boolean overlaps(Rectangle a, Rectangle b) {
        // Rectangles are considered overlapping when any of the interior points are shared
        return !(b.minY() >= a.maxY() || b.minX() >= a.maxX() || b.maxY() <= a.minY() || b.maxX() <= a.minX());
    }

    public static boolean contains(Rectangle a, Rectangle b) {
        // a contains b iff no points of b lie in the exterior of a, and at least one point of the interior of b lies in the interior of a
        return (a.maxY() >= b.maxY() && a.minY() <= b.minY() && a.maxX() >= b.maxX() && a.minX() <= b.minX());
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

    public static int getWidth(Rectangle a) {
        return Math.abs(a.maxX() - a.minX());
    }

    public static int getHeight(Rectangle a) {
        return Math.abs(a.maxY() - a.minY());
    }

    public static int getShortestSide(Rectangle a) {
        return Math.min(getHeight(a), getWidth(a));
    }

    public static int getArea(Rectangle a) {
        return getHeight(a) * getWidth(a);
    }

    public static Vector2d getCenter(Rectangle a) {
        return Vector2d.from((double) (a.maxX() + a.minX()) / 2, (double) (a.maxY() + a.minY()) / 2);
    }

    public static double getDistanceSquaredToRectangle(Vector2d point, Rectangle rect) {
        Vector2d center = getCenter(rect);
        double pointXLength = Math.max(Math.abs(point.getX() - center.getX()) - (double) getWidth(rect) / 2, 0);
        double pointYLength = Math.max(Math.abs(point.getY() - center.getY()) - (double) getHeight(rect) / 2, 0);
        return Math.pow(pointXLength, 2) + Math.pow(pointYLength, 2);
    }

    public static double getDistanceSquaredToRectangle(Vector3d point, Rectangle rect) {
        return getDistanceSquaredToRectangle(point.toVector2(true), rect);
    }

    /**
     * Subtracts RectB from RectA returning at most 4 new rectangles
     *
     *   ****************      ****************      ****************
     *   |      RA      |      |              |      |      1       |
     *   |    ******    |      |    ******    |      |**************|
     *   |    | RB |    |  -]  |    |Hole|    |  -]  |  2 |Hole| 3  |
     *   |    ******    |      |    ******    |      |**************|
     *   |              |      |              |      |      4       |
     *   ****************      ****************      ****************
     *
     * @param a Rectangle a
     * @param b Rectangle b
     * @return A List of rectangles that make up the remainder of Rectangle A
     */
    public static List<BasicRectangle> subtractRectangles(Rectangle a, Rectangle b) {
        // Handle edge cases
        if (equals(a, b)) {
            return new ArrayList<>();
        }
        if (contains(b, a)) {
            return new ArrayList<>();
        }
        if (!overlaps(a, b)) {
            return new ArrayList<BasicRectangle>() {{
                add(new BasicRectangle(a.getTopLeftCorner(), a.getBottomRightCorner()));
            }};
        }

        ArrayList<BasicRectangle> results = new ArrayList<>();
        // Get top rectangle
        if (b.maxY() < a.maxY()) {
            results.add(new BasicRectangle(a.getTopLeftCorner(), Vector2i.from(a.maxX(), b.maxY())));
        }
        // Get left rectangle
        if (b.minX() > a.minX()) {
            results.add(new BasicRectangle(Vector2i.from(a.minX(), Math.min(a.maxY(), b.maxY())),
                                           Vector2i.from(b.minX(), Math.max(a.minY(), b.minY()))));
        }
        // Get right rectangle
        if (b.maxX() < a.maxX()) {
            results.add(new BasicRectangle(Vector2i.from(b.maxX(), Math.min(a.maxY(), b.maxY())),
                                           Vector2i.from(a.maxX(), Math.max(a.minY(), b.minY()))));
        }
        // Get bottom rectangle
        if (b.minY() > a.minY()) {
            results.add(new BasicRectangle(Vector2i.from(a.minX(), b.minY()) , a.getBottomRightCorner()));
        }

        return results;
    }

    /**
     * Checks to see if a rectangle is fully contained within another set of rectangles
     * @param target The rectangle to check
     * @param set The set of rectangles
     * @return True if the target is fully within the area covered by the set, otherwise false
     */
    public static boolean rectangleContainedInSet(Rectangle target, Set<? extends Rectangle> set) {
        List<Rectangle> remaining = new ArrayList<Rectangle>() {{
            add(target);
        }};

        for (Rectangle rect : set) {
            ListIterator<Rectangle> iterator = remaining.listIterator();
            while (iterator.hasNext()) {
                Rectangle tar = iterator.next();
                iterator.remove();
                subtractRectangles(tar, rect).forEach(iterator::add);
            }
        }
        return remaining.isEmpty();
    }

    public static double getDistanceToPlotSquared(Vector2i point, Rectangle plot) {
        int lengthX = getWidth(plot);
        int lengthZ = getHeight(plot);

        double centerX = (double) (plot.minX() + plot.maxX()) / 2;
        double centerZ = (double) (plot.minY() + plot.maxY()) / 2;

        double pointXLength = Math.max(Math.abs(point.getX() - centerX) - (double) lengthX / 2, 0);
        double pointYLength = Math.max(Math.abs(point.getY() - centerZ) - (double) lengthZ / 2, 0);

        return Math.pow(pointXLength, 2) + Math.pow(pointYLength, 2);
    }
}
