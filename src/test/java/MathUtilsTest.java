import com.atherys.towns.util.MathUtils;
import com.flowpowered.math.vector.Vector2i;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void testPlotBordering() {
        Vector2i southWestA = Vector2i.from(88, 11);
        Vector2i northEastA = Vector2i.from(91, 7);

        Vector2i southWestB = Vector2i.from(92, 9);
        Vector2i northEastB = Vector2i.from(96, 5);

        boolean borders = MathUtils.borders(southWestB, northEastB, southWestA, northEastA);

        Assert.assertEquals(borders, true);
    }
}
