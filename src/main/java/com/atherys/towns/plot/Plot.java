package com.atherys.towns.plot;

import com.atherys.core.views.Viewable;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.plot.flags.Extent;
import com.atherys.towns.plot.flags.Flag;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.views.PlotView;
import com.flowpowered.math.vector.Vector3d;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class Plot extends AreaObject<Town> implements Viewable<PlotView> {

    private PlotDefinition definition;
    private PlotFlags flags;

    private String name;

    Plot(UUID uuid) {
        super ( uuid );
    }

    private Plot (PlotDefinition definition, Town town, String name ) {
        super ( UUID.randomUUID() );
        this.definition = definition;
        this.setParent(town);
        flags = PlotFlags.regular();
        this.name = name;
        PlotManager.getInstance().add(this);
        PlotManager.getInstance().saveOne(this);
    }

    public static Plot create ( PlotDefinition define, Town town, String name ) {
        return new Plot(define, town, name);
    }

    public PlotBuilder builder() { return new PlotBuilder(); }

    public static PlotBuilder fromUUID(UUID uuid) { return new PlotBuilder(uuid); }

    public boolean isResidentAllowedTo (Resident res, Flag flag) { return this.flags.isAllowed(res, flag, this); }

    public PlotDefinition getDefinition() { return definition; }

    public void setDefinition(PlotDefinition definition) {
        this.definition = definition;
    }

    public PlotFlags getFlags() { return flags; }

    public void setFlags( PlotFlags flags ) { this.flags = flags.copy(); }

    public void setFlag( Flag flag, Extent ext ) {
        this.flags.set(flag, ext);
    }

    public Town getTown() {
        return super.getParent().get();
    }

    public void showBorder(Player p) {
        for ( LineSegment2D edge : definition.edges() ) {
            for ( int i = 0; i <= edge.length(); i+=2 ) {
                Point2D twoD = interpolationByDistance(edge, i);
                Vector3d loc = new Vector3d( twoD.x(), p.getLocation().getBlockY() + 1, twoD.y() );
                p.spawnParticles(ParticleEffect.builder()
                        .velocity(Vector3d.from(0, 0.08, 0))
                        .type(ParticleTypes.BARRIER)
                        .quantity(1)
                        .build(), loc);
            }
        }
    }

    private static Point2D interpolationByDistance(LineSegment2D l, double d) {
        if ( d == 0 ) return l.firstPoint();
        if ( d == l.length() ) return l.lastPoint();
        double len = l.length();
        double ratio = d/len;
        double x = ratio*l.lastPoint().x() + (1.0 - ratio)*l.firstPoint().x();
        double y = ratio*l.lastPoint().y() + (1.0 - ratio)*l.firstPoint().y();
        return new Point2D( x, y );
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

    @Override
    public boolean contains(World w, double x, double y) {
        return definition.getWorld().equals(w) && definition.contains(x, y);
    }

    @Override
    public boolean contains( World w, Point2D point) {
        return definition.getWorld().equals(w) && definition.contains(point);
    }

    @Override
    public boolean contains ( Location<World> loc ) {
        return definition.contains(loc);
    }

    public void remove() {
        // TODO: When a plot is claimed and unclaimed, the plot is still stored in the database
        PlotManager.getInstance().remove(this);
    }

    @Override
    public Optional<PlotView> createView() {
        return Optional.of( new PlotView(this) );
    }
}
