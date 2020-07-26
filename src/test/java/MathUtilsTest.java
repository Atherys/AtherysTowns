import com.atherys.towns.util.BasicRectangle;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

public class MathUtilsTest {

    // Declare some common rectangles for testing

    // Rectangles connected on the corner
    // +---+      borders: False
    // | a |      overlaps: False
    // +-------+  contains: False
    //     | b |  touches: True
    //     +---+
    private final BasicRectangle touchCornerA = new BasicRectangle(Vector2i.from(88, 11), Vector2i.from(92, 7));
    private final BasicRectangle touchCornerB = new BasicRectangle(Vector2i.from(92, 7), Vector2i.from(96, 5));


    // Rectangles bordering on the bottom
    // +---+      borders: True
    // | a |      overlaps: False
    // +-+-+-+    contains: False
    //   | b |    touches: True
    //   +---+
    private final BasicRectangle borderBottomA = new BasicRectangle(Vector2i.from(88, 11), Vector2i.from(94, 7));
    private final BasicRectangle borderBottomB = new BasicRectangle(Vector2i.from(92, 7), Vector2i.from(96, 5));

    // Rectangles bordering on the side
    // +---+---+   borders: True
    // | a | b |   overlaps: False
    // +---+---+   contains: False
    //             touches: True
    private final BasicRectangle borderSideA  = new BasicRectangle(Vector2i.from(-4, -2), Vector2i.from(-3, -4));
    private final BasicRectangle borderSideB  = new BasicRectangle(Vector2i.from(-3, -2), Vector2i.from(-2, -4));

    // Disjoint Rectangles
    //  +---+       borders: False
    //  | a |       overlaps: False
    //  +---+       contains: False
    //      +---+   touches: False
    //      | b |
    //      +---+
    private final BasicRectangle disjointA = new BasicRectangle(Vector2i.from(-6, 2), Vector2i.from(-5, -1));
    private final BasicRectangle disjointB = new BasicRectangle(Vector2i.from(-4, -3), Vector2i.from(2, -5));

    // Intersecting rectangles
    //  +-----+    borders: False
    //  |  a  |    overlaps: True
    //  |  +----+  contains: False
    //  +--|--+ |  touches: False
    //     | b  |
    //     +----+
    private final BasicRectangle intersectA = new BasicRectangle(Vector2i.from(2, 4), Vector2i.from(4, 2));
    private final BasicRectangle intersectB = new BasicRectangle(Vector2i.from(3, 3), Vector2i.from(6, 1));

    // Containing rectangles
    //  +-------+  borders: False
    //  |   a   |  overlaps: True
    //  | +---+ |  contains: (a,b) True (b,a) False
    //  | | b | |  touches: False
    //  | +---+ |
    //  +-------+
    private final BasicRectangle containsA = new BasicRectangle(Vector2i.from(2, 4), Vector2i.from(6, 1));
    private final BasicRectangle containsB = new BasicRectangle(Vector2i.from(3, 3), Vector2i.from(5, 2));

    @Test
    public void testPlotBordering() {
        // Rectangles connected on the corner
        Assert.assertFalse("Rectangles touching on corners only do not border",
                MathUtils.borders(touchCornerA, touchCornerB));

        // Rectangles bordering on the bottom
        Assert.assertTrue(MathUtils.borders(borderBottomA, borderBottomB));
        Assert.assertTrue(MathUtils.borders(borderBottomB, borderBottomA));

        // Rectangles bordering on the side
        Assert.assertTrue(MathUtils.borders(borderSideA, borderSideB));
        Assert.assertTrue(MathUtils.borders(borderSideB, borderSideA));

        // Disjoint Rectangles
        Assert.assertFalse(MathUtils.borders(disjointA, disjointB));
        Assert.assertFalse(MathUtils.borders(disjointB, disjointA));

        // Intersecting rectangles
        Assert.assertFalse(MathUtils.borders(intersectA, intersectB));
        Assert.assertFalse(MathUtils.borders(intersectB, intersectA));

        // Containing rectangles
        Assert.assertFalse(MathUtils.borders(containsA, containsB));
        Assert.assertFalse(MathUtils.borders(containsB, containsA));
    }

    @Test
    public void testTouches() {
        // Rectangles connected on the corner
        Assert.assertTrue(MathUtils.touches(touchCornerA, touchCornerB));
        Assert.assertTrue(MathUtils.touches(touchCornerB, touchCornerA));

        // Rectangles bordering on the bottom
        Assert.assertTrue(MathUtils.touches(borderBottomA, borderBottomB));
        Assert.assertTrue(MathUtils.touches(borderBottomB, borderBottomA));

        // Rectangles bordering on the side
        Assert.assertTrue(MathUtils.touches(borderSideA, borderSideB));
        Assert.assertTrue(MathUtils.touches(borderSideB, borderSideA));

        // Disjoint Rectangles
        Assert.assertFalse(MathUtils.touches(disjointA, disjointB));
        Assert.assertFalse(MathUtils.touches(disjointB, disjointA));

        // Intersecting rectangles
        Assert.assertFalse(MathUtils.touches(intersectA, intersectB));
        Assert.assertFalse(MathUtils.touches(intersectB, intersectA));

        // Containing rectangles
        Assert.assertFalse(MathUtils.touches(containsA, containsB));
        Assert.assertFalse(MathUtils.touches(containsB, containsA));
    }

    @Test
    public void testOverlap() {
        // Rectangles connected on the corner
        Assert.assertFalse(MathUtils.overlaps(touchCornerA, touchCornerB));
        Assert.assertFalse(MathUtils.overlaps(touchCornerB, touchCornerA));

        // Rectangles bordering on the bottom
        Assert.assertFalse(MathUtils.overlaps(borderBottomA, borderBottomB));
        Assert.assertFalse(MathUtils.overlaps(borderBottomB, borderBottomA));

        // Rectangles bordering on the side
        Assert.assertFalse(MathUtils.overlaps(borderSideA, borderSideB));
        Assert.assertFalse(MathUtils.overlaps(borderSideB, borderSideA));

        // Disjoint Rectangles
        Assert.assertFalse(MathUtils.overlaps(disjointA, disjointB));
        Assert.assertFalse(MathUtils.overlaps(disjointB, disjointA));

        // Intersecting rectangles
        Assert.assertTrue(MathUtils.overlaps(intersectA, intersectB));
        Assert.assertTrue(MathUtils.overlaps(intersectB, intersectA));

        // Containing rectangles
        Assert.assertTrue(MathUtils.overlaps(containsA, containsB));
        Assert.assertTrue(MathUtils.overlaps(containsB, containsA));
    }

    @Test
    public void testContains() {
        // Rectangles connected on the corner
        Assert.assertFalse(MathUtils.contains(touchCornerA, touchCornerB));
        Assert.assertFalse(MathUtils.contains(touchCornerB, touchCornerA));

        // Rectangles bordering on the bottom
        Assert.assertFalse(MathUtils.contains(borderBottomA, borderBottomB));
        Assert.assertFalse(MathUtils.contains(borderBottomB, borderBottomA));

        // Rectangles bordering on the side
        Assert.assertFalse(MathUtils.contains(borderSideA, borderSideB));
        Assert.assertFalse(MathUtils.contains(borderSideB, borderSideA));

        // Disjoint Rectangles
        Assert.assertFalse(MathUtils.contains(disjointA, disjointB));
        Assert.assertFalse(MathUtils.contains(disjointB, disjointA));

        // Intersecting rectangles
        Assert.assertFalse(MathUtils.contains(intersectA, intersectB));
        Assert.assertFalse(MathUtils.contains(intersectB, intersectA));

        // Containing rectangles
        Assert.assertTrue(MathUtils.contains(containsA, containsB));
        Assert.assertFalse(MathUtils.contains(containsB, containsA));
    }

    @Test
    public void testEquals() {
        // Test same Rectangle
        Assert.assertTrue(MathUtils.equals(containsA, containsA));

        // Test copied rectangle
        BasicRectangle test = new BasicRectangle(containsA.getTopLeftCorner(), containsA.getBottomRightCorner());
        Assert.assertTrue(MathUtils.equals(containsA, test));
        Assert.assertTrue(MathUtils.equals(test, containsA));
    }

    @Test
    public void testWidth() {
        // Test touchCornerA
        Assert.assertEquals(4, MathUtils.getWidth(touchCornerA));

        // Test containsA
        Assert.assertEquals(4, MathUtils.getWidth(containsA));

        // Test containsB
        Assert.assertEquals(2, MathUtils.getWidth(containsB));
    }

    @Test
    public void testHeight() {
        // Test touchCornerA
        Assert.assertEquals(4, MathUtils.getHeight(touchCornerA));

        // Test containsA
        Assert.assertEquals(3, MathUtils.getHeight(containsA));

        // Test containsB
        Assert.assertEquals(1, MathUtils.getHeight(containsB));
    }

    @Test
    public void testShortestSide() {
        // Test touchCornerA
        Assert.assertEquals(4, MathUtils.getShortestSide(touchCornerA));

        // Test containsA
        Assert.assertEquals(3, MathUtils.getShortestSide(containsA));

        // Test containsB
        Assert.assertEquals(1, MathUtils.getShortestSide(containsB));
    }

    @Test
    public void testArea() {
        // Test touchCornerA
        Assert.assertEquals(16, MathUtils.getArea(touchCornerA));

        // Test containsA
        Assert.assertEquals(12, MathUtils.getArea(containsA));

        // Test containsB
        Assert.assertEquals(2, MathUtils.getArea(containsB));
    }

    @Test
    public void testRectangleSubtract() {
        List<BasicRectangle> rects;

        // Subtract Intersecting
        rects = MathUtils.subtractRectangles(intersectA, intersectB);
        Assert.assertEquals(2, rects.size());
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(2,4), Vector2i.from(4,3))));
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(2,3), Vector2i.from(3,2))));

        // Subtract containsB from containsA
        rects = MathUtils.subtractRectangles(containsA, containsB);
        Assert.assertEquals(4, rects.size());
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(2,4), Vector2i.from(6,3))));
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(2,3), Vector2i.from(3,2))));
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(2,2), Vector2i.from(6,1))));
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(5,3), Vector2i.from(6,2))));

        // Subtract half covered
        BasicRectangle a = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 10));
        BasicRectangle left = new BasicRectangle(Vector2i.from(5,20), Vector2i.from(15,5));
        BasicRectangle right = new BasicRectangle(Vector2i.from(15,20), Vector2i.from(30,5));
        // Left from A
        rects = MathUtils.subtractRectangles(a, left);
        Assert.assertEquals(1, rects.size());
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(15,15), Vector2i.from(25,10))));
        // Right from A
        rects = MathUtils.subtractRectangles(a, right);
        Assert.assertEquals(1, rects.size());
        Assert.assertTrue(rects.contains(new BasicRectangle(Vector2i.from(10,15), Vector2i.from(15,10))));

        // Subtract contains
        rects = MathUtils.subtractRectangles(containsB, containsA);
        Assert.assertEquals(0, rects.size());

        // Subtract Disjoint
        rects = MathUtils.subtractRectangles(disjointA, disjointB);
        Assert.assertEquals(1, rects.size());
        Assert.assertTrue(rects.contains(disjointA));

        // Subtract Equals
        rects = MathUtils.subtractRectangles(intersectA, intersectA);
        Assert.assertEquals(0, rects.size());
    }

    @Test
    public void testRectangleContainedInSet() {
        HashSet<BasicRectangle> rects;
        BasicRectangle target;

        // Target contained within single rectangle in set
        target = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 10));
        rects = new HashSet<BasicRectangle>() {{
            add(new BasicRectangle(Vector2i.from(5,20), Vector2i.from(30,5)));
        }};
        Assert.assertTrue(MathUtils.rectangleContainedInSet(target, rects));

        // Covered by Two rectangles
        target = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 10));
        rects = new HashSet<BasicRectangle>() {{
            add(new BasicRectangle(Vector2i.from(5,20), Vector2i.from(15,5)));
            add(new BasicRectangle(Vector2i.from(15,20), Vector2i.from(30,5)));
        }};
        Assert.assertTrue(MathUtils.rectangleContainedInSet(target, rects));

        // Uncovered Hole in Center
        target = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 0));
        rects = new HashSet<BasicRectangle>() {{
            add(new BasicRectangle(Vector2i.from(5,20), Vector2i.from(15,-5)));
            add(new BasicRectangle(Vector2i.from(20,20), Vector2i.from(30,-5)));
        }};
        Assert.assertFalse(MathUtils.rectangleContainedInSet(target, rects));

        // Disjoint Rectangles
        rects = new HashSet<BasicRectangle>() {{
            add(disjointB);
        }};
        Assert.assertFalse(MathUtils.rectangleContainedInSet(disjointA, rects));
    }

    @Test
    public void testPointInRectangle() {
        BasicRectangle rect = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 0));

        // Test a corner
        Assert.assertTrue(MathUtils.pointInRectangle(Vector2d.from(10, 15), rect));
        // Test a point on the edge
        Assert.assertTrue(MathUtils.pointInRectangle(Vector2d.from(10, 0), rect));
        // Test an external point
        Assert.assertFalse(MathUtils.pointInRectangle(Vector2d.from(0, 0), rect));
    }

    @Test
    public void testGetCenter() {
        BasicRectangle rect = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 0));
        Assert.assertEquals(Vector2d.from(17.5, 7.5), MathUtils.getCenter(rect));
    }

    @Test
    public void testGetDistanceSquaredToRectangle() {
        BasicRectangle rect = new BasicRectangle(Vector2i.from(10, 15), Vector2i.from(25, 0));

        // A point on the border should return 0
        Assert.assertEquals(0, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(10,15), rect));
        // A point within the border should return 0
        Assert.assertEquals(0, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(16,7), rect));

        // A point directly above/below/left/right should return regardless of the change in x/y
        // points below
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(18,-5), rect));
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(10,-5), rect));
        // points above
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(12,20), rect));
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(25,20), rect));
        // points left
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(5,15), rect));
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(5,6), rect));
        // points right
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(30,11), rect));
        Assert.assertEquals(25, (int) MathUtils.getDistanceSquaredToRectangle(Vector2d.from(30,0), rect));
    }

    @Test
    public void testDistanceSquaredBetweenPoints() {
        Assert.assertEquals(200 , (int) MathUtils.getDistanceBetweenPointsSquared(Vector3d.from(0, 0, 0),
                Vector3d.from(10, 0, 10)));

        Assert.assertEquals(133664 , (int) MathUtils.getDistanceBetweenPointsSquared(Vector3d.from(-20, 0, -50),
                Vector3d.from(200, 0, 242)));
    }
}
