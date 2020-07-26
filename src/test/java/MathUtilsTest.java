import com.atherys.towns.util.BasicRectangle;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import org.junit.Assert;
import org.junit.Test;

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

    //Vector Range Testing
    private final Vector3d extraneousVector = new Vector3d(2501, -224, -9281);
    private final Vector3d middleVector = new Vector3d(5,74,-14);
    private final Vector3d lowestVector = new Vector3d(-14,24,-204);
    private final Vector3d highestVector = new Vector3d(25,115,539);

    //Vector3i to 2i conversion
    private final Vector2i extraneous2iVector = MathUtils.vec3iToVec2i(extraneousVector.toInt());
    private final Vector2i middle2iVector = MathUtils.vec3iToVec2i(middleVector.toInt());
    private final Vector2i lowest2iVector = MathUtils.vec3iToVec2i(lowestVector.toInt());
    private final Vector2i highest2iVector = MathUtils.vec3iToVec2i(highestVector.toInt());

    @Test
    public void testFitsInRange() {
        // Int Range Testing Variables
        int lowestPoint = -14;
        int highestPoint = 117;
        int middleNumber = -4;
        int extraneousNumber = 2093;

        // Middle number is between lowest and highest
        Assert.assertTrue(MathUtils.fitsInRange(middleNumber, lowestPoint, highestPoint));
        // Middle number is between lowest and highest
        Assert.assertTrue(MathUtils.fitsInRange((double) middleNumber, lowestPoint, highestPoint));
        // Extraneous number is not between lowest and highest
        Assert.assertFalse(MathUtils.fitsInRange(extraneousNumber, lowestPoint, highestPoint));
        // Extraneous number is not between lowest and highest
        Assert.assertFalse(MathUtils.fitsInRange((double) extraneousNumber, lowestPoint, highestPoint));
    }

    @Test
    public void testVectorFitsInRange() {
        // Middle vector is between lowest and highest
        Assert.assertTrue(MathUtils.vectorFitsInRange(middleVector, lowestVector.toInt(), highestVector.toInt()));
        // Middle vector is between lowest and highest
        Assert.assertTrue(MathUtils.vectorFitsInRange(middleVector.toInt(), lowestVector.toInt(), highestVector.toInt()));

        // Middle vector is between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertTrue(MathUtils.vectorFitsInRange(middle2iVector, lowest2iVector, highest2iVector));

        // Extraneous vector is not between lowest and highest
        Assert.assertFalse(MathUtils.vectorFitsInRange(extraneousVector, lowestVector.toInt(), highestVector.toInt()));
        // Extraneous vector is not between lowest and highest
        Assert.assertFalse(MathUtils.vectorFitsInRange(extraneousVector.toInt(), lowestVector.toInt(), highestVector.toInt()));

        // Extraneous vector is not between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertFalse(MathUtils.vectorFitsInRange(extraneous2iVector, lowest2iVector, highest2iVector));
    }

    @Test
    public void testVector2DFitsInRange() {
        // Middle vector is between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertFalse(MathUtils.vectorXZFitsInRange(middleVector, lowest2iVector, highest2iVector));
        // Middle vector is between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertFalse(MathUtils.vectorXZFitsInRange(middleVector.toInt(), lowest2iVector, highest2iVector));

        // Extraneous vector is not between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertFalse(MathUtils.vectorXZFitsInRange(extraneousVector, lowest2iVector, highest2iVector));
        // Extraneous vector is not between lowest and highest (Also is a test for 3i to 2i conversion)
        Assert.assertFalse(MathUtils.vectorXZFitsInRange(extraneousVector.toInt(), lowest2iVector, highest2iVector));
    }

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

}
