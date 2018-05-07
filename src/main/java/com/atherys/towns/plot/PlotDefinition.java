package com.atherys.towns.plot;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.town.Town;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import java.util.Optional;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Rectangle2D;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlotDefinition extends Rectangle2D {

    private World world;

    public PlotDefinition(World world, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.world = world;
    }

    private PlotDefinition(World world, Vector3d loc1, Vector3d loc2) {
        super(new Point2D(loc1.getX(), loc1.getZ()), new Point2D(loc2.getX(), loc2.getZ()));
        this.world = world;
    }

    private PlotDefinition(World world, Rectangle2D rect) {
        super(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        this.world = world;
    }

    public static Optional<PlotDefinition> fromPlayer(Player player, Town town)
        throws DefinitionNotValidException, DefinitionNotPresentException {
        return Optional.of(new PlotDefinition(player.getWorld(), checkPlayer(player, town)));
    }

    public static Optional<PlotDefinition> fromChunk(Player player, Town town, Chunk chunk)
        throws DefinitionNotValidException, DefinitionNotPresentException {

        Vector3i loc1 = chunk.getPosition().mul(16);
        Vector3i loc2 = loc1.add(15, 0, 15);

        return Optional.of(new PlotDefinition(player.getWorld(),
            checkPlayer(player, town, loc1.toDouble(), loc2.toDouble())));
    }

    boolean contains(Location<World> loc) {
        return loc.getExtent().equals(world) && super
            .contains(loc.getPosition().getX(), loc.getPosition().getZ());
    }

    /*private static Location[] arrange ( Location loc1, Location loc2 ) {
        if ( loc1.getBlockPosition().getX() > loc2.getBlockPosition().getX() && loc1.getBlockPosition().getZ() > loc2.getBlockPosition().getZ() ) {
            return new Location[]{ loc1, loc2 };
        } else {
            return new Location[]{ loc2, loc1 };
        }
    }*/

    // Smaller one first
    // Larger one second
    private static Tuple<Vector3d, Vector3d> format(Vector3d A, Vector3d B) {
        if (A.getX() >= B.getX() && A.getZ() >= B.getZ()) {
            return Tuple.of(B, A.add(1, 0, 1));

        } else if (A.getX() <= B.getX() && A.getZ() <= B.getZ()) {
            return Tuple.of(A, B.add(1, 0, 1));

        } else if (A.getX() <= B.getX() && A.getZ() >= B.getZ()) {
            return Tuple.of(A.add(0, 0, 1), B.add(1, 0, 0));

        } else /*if ( A.getX() >= B.getX() && A.getZ() <= B.getZ() )*/ {
            return Tuple.of(A.add(1, 0, 0), B.add(0, 0, 1));
        }
    }

    public PlotDefinition shrink(double x, double y, double width, double height) {
        return new PlotDefinition(world, super.x0 + x, super.y0 + y, super.w - 2 * width,
            super.h - 2 * height);
    }

    public boolean intersects(PlotDefinition define) {
        PlotDefinition shrink = define.shrink(0.1, 0.1, 0.1, 0.1);
        for (Point2D vertex : shrink.vertices()) {
            if (this.contains(vertex)) {
                return true;
            }
        }
        return false;
    }

    /*public boolean isBordering ( Rectangle2D definition ) {
        return isBordering( new PlotDefinition( definition.world, definition) );
    }*/

    public boolean isBordering(PlotDefinition definition) {
        if (!this.world.equals(definition.world)) {
            return false;
        }
        for (LineSegment2D edge : definition.edges()) {
            for (Point2D point : this.vertices()) {
                if (edge.contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkBordering(Town town, PlotDefinition test) {
        if (town == null) {
            return true;
        }
        for (Plot p : town.getPlots()) {
            if (p.getDefinition().isBordering(test)) {
                return true;
            }
        }
        return false;
    }

    private static Rectangle2D checkPlayer(Player player, Town town, Vector3d pos1, Vector3d pos2)
        throws DefinitionNotValidException {
        Tuple<Vector3d, Vector3d> positions = format(pos1, pos2);
        PlotDefinition test = new PlotDefinition(player.getWorld(),
            new Rectangle2D(new Point2D(positions.getFirst().getX(), positions.getFirst().getZ()),
                new Point2D(positions.getSecond().getX(), positions.getSecond().getZ())));

        double size_x = test.getWidth();
        double size_z = test.getHeight();

        if (size_x < AtherysTowns.getConfig().TOWN.MIN_PLOT_SIZE || size_z < AtherysTowns
            .getConfig().TOWN.MIN_PLOT_SIZE) {
            TownMessage.warn(player, Text.of(
                "Sides of plot must be at least " + AtherysTowns.getConfig().TOWN.MIN_PLOT_SIZE
                    + " blocks wide!"));
            throw new DefinitionNotValidException();
        }
        if (size_x * size_z > AtherysTowns.getConfig().TOWN.MAX_PLOT_AREA) {
            TownMessage.warn(player, Text.of(
                "Maximum area of a plot is " + AtherysTowns.getConfig().TOWN.MAX_PLOT_AREA
                    + " blocks!"));
            throw new DefinitionNotValidException();
        }
        if (PlotManager.getInstance()
            .checkIntersection(new PlotDefinition(player.getWorld(), test))) {
            TownMessage.warn(player, Text.of("Plot must not intersect other plots!"));
            throw new DefinitionNotValidException();
        }
        if (!checkBordering(town, test)) {
            TownMessage.warn(player, Text.of(
                "Plot definition must border at least 1 other plot that already belongs to the town."));
            throw new DefinitionNotValidException();
        }
        if (!test.contains(player.getLocation().getPosition().getX(),
            player.getLocation().getPosition().getZ())) {
            TownMessage
                .warn(player, Text.of("You must be within the plot definition when claiming!"));
            throw new DefinitionNotValidException();
        }

        return test;
    }

    private static Rectangle2D checkPlayer(Player player, Town town)
        throws DefinitionNotValidException, DefinitionNotPresentException {
        Optional<Object> loc1Opt = TownsValues
            .get(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_1ST);
        Optional<Object> loc2Opt = TownsValues
            .get(player.getUniqueId(), TownsValues.TownsKey.PLOT_SELECTOR_2ND);

        if (!loc1Opt.isPresent() || !loc2Opt.isPresent()) {
            TownMessage.warn(player, Text.of(
                "You must use the plot select tool ( /p tool ), right-click at your first location and left-click and your second location in order to set up a plot definition."));
            throw new DefinitionNotPresentException();
        }

        Location loc1 = Location.class.cast(loc1Opt.get());
        Location loc2 = Location.class.cast(loc2Opt.get());

        if (!loc1.getExtent().equals(loc2.getExtent())) {
            TownMessage
                .warn(player, Text.of("Corners of definition must be within the same world!"));
            throw new DefinitionNotValidException();
        }

        Vector3d pos1 = loc1.getBlockPosition().toDouble();
        Vector3d pos2 = loc2.getBlockPosition().toDouble();

        return checkPlayer(player, town, pos1, pos2);
    }

    public World getWorld() {
        return this.world;
    }

    public static class DefinitionNotPresentException extends Exception {

    }

    public static class DefinitionNotValidException extends Exception {

    }
}
