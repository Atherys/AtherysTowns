import com.atherys.towns.util.BasicRectangle;
import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    //Plot A
    // 1. Should border with B
    // 2. Should not border with C or D
    // 3. Should not overlap any
    private final Vector2i southWestA = Vector2i.from(88, 11);
    private final Vector2i northEastA = Vector2i.from(91, 7);

    //Plot B
    // 1. Should border with A and C
    // 2. Should not border with C or D
    // 3. Should overlap with C
    // 4. Should not overlap with A or D
    private final Vector2i southWestB = Vector2i.from(85, 6);
    private final Vector2i northEastB = Vector2i.from(89, 2);

    //Plot C
    // 1. Should not border with B
    // 2. should overlap with B
    // 3. Should not overlap with A or D
    private final Vector2i southWestC = Vector2i.from(82, 4);
    private final Vector2i northEastC = Vector2i.from(87, -2);

    //Plot D
    // 1. Should not border with any
    // 2. Should not overlap with any
    private final Vector2i southWestD = Vector2i.from(85, -10);
    private final Vector2i northEastD = Vector2i.from(89, -15);

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
    public void testPlotOverlapping() {
        // Plot A Does Not Overlap B
        //Assert.assertFalse(MathUtils.overlaps(southWestA, northEastA, southWestB, northEastB));
        // Plot A Does Not Overlap C
        //Assert.assertFalse(MathUtils.overlaps(southWestA, northEastA, southWestC, northEastC));
        // Plot A Does Not Overlap D
        //Assert.assertFalse(MathUtils.overlaps(southWestA, northEastA, southWestD, northEastD));
        // Plot B Overlaps C
        //Assert.assertTrue(MathUtils.overlaps(southWestB, northEastB, southWestC, northEastC));
        // Plot B Does Not Overlap D
        //Assert.assertFalse(MathUtils.overlaps(southWestB, northEastB, southWestD, northEastD));
        // Plot C Does Not Overlap D
        //Assert.assertFalse(MathUtils.overlaps(southWestC, northEastC, southWestD, northEastD));
    }

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
    public void testXLength() {
        // Plot A Known X Length = 3
        Assert.assertEquals(3, MathUtils.getXLength(northEastA, southWestA));
        Assert.assertEquals(3, MathUtils.getXLength(southWestA, northEastA));
    }

    @Test
    public void testZLength() {
        // Plot A Known Z Length = 4
        Assert.assertEquals(4, MathUtils.getZLength(northEastA, southWestA));
        Assert.assertEquals(4, MathUtils.getZLength(southWestA, northEastA));
    }

    @Test
    public void testPlotBordering() {
        BasicRectangle a, b;

        // Rectangles bordering on the bottom
        // +---+
        // | a |
        // +-+-+-+
        //   | b |
        //   +---+
        a = new BasicRectangle(Vector2i.from(88, 11), Vector2i.from(94, 7));
        b = new BasicRectangle(Vector2i.from(92, 7), Vector2i.from(96, 5));

        Assert.assertTrue(MathUtils.borders(a, b));
        Assert.assertTrue("Borders should be commutative", MathUtils.borders(b, a));

        // Rectangles bordering on the side
        // +---+---+
        // | a | b |
        // +---+---+
        a = new BasicRectangle(Vector2i.from(-4, -2), Vector2i.from(-3, -4));
        b = new BasicRectangle(Vector2i.from(-3, -2), Vector2i.from(-2, -4));

        Assert.assertTrue(MathUtils.borders(a, b));
        Assert.assertTrue(MathUtils.borders(b, a));

        // Rectangles connected on the corner
        // +---+
        // | a |
        // +-------+
        //     | b |
        //     +---+
        a = new BasicRectangle(Vector2i.from(88, 11), Vector2i.from(92, 7));
        b = new BasicRectangle(Vector2i.from(92, 7), Vector2i.from(96, 5));

        Assert.assertFalse("Rectangles touching on corners only do not border", MathUtils.borders(a, b));

        // Disjoint Rectangles
        //
        //  +---+
        //  | a |
        //  +---+
        //      +---+
        //      | b |
        //      +---+
        a = new BasicRectangle(Vector2i.from(-6, 2), Vector2i.from(-5, -1));
        b = new BasicRectangle(Vector2i.from(-4, -3), Vector2i.from(2, -5));

        Assert.assertFalse(MathUtils.borders(a, b));
    }

    @Test
    public void testTouches() {
        BasicRectangle a, b;

        a = new BasicRectangle(Vector2i.from(2, 4), Vector2i.from(4, 2));
        b = new BasicRectangle(Vector2i.from(3, 6), Vector2i.from(6, 4));

        Assert.assertTrue(MathUtils.touches(a, b));
        Assert.assertFalse(MathUtils.borders(a, b));
    }

}
