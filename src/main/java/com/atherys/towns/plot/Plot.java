package com.atherys.towns.plot;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.flowpowered.math.vector.Vector3d;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class Plot extends AreaObject<Town> {

    public static class Builder {

        Plot plot;

        Builder () {
            this.plot = new Plot(UUID.randomUUID());
        }

        Builder ( UUID uuid ) {
            this.plot = new Plot(uuid);
        }

        public Plot.Builder definition(PlotDefinition definition) {
            plot.setDefinition(definition);
            return this;
        }

        public Plot.Builder town ( Town town ) {
            plot.setParent(town);
            return this;
        }

        public Plot.Builder flags ( PlotFlags flags ) {
            plot.setFlags(flags);
            return this;
        }

        public Plot.Builder name(String name) {
            plot.setName(name);
            return this;
        }

        public Plot build() {
            PlotManager.getInstance().add(plot);
            return plot;
        }

    }

    private PlotDefinition definition;
    private PlotFlags flags;

    private String name;

    private Plot ( UUID uuid ) {
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

    public Plot.Builder builder() { return new Plot.Builder(); }

    public static Plot.Builder fromUUID(UUID uuid) { return new Plot.Builder(uuid); }

    public boolean isResidentAllowedTo (Resident res, PlotFlags.Flag flag) { return this.flags.isAllowed(res, flag, this); }

    public PlotDefinition getDefinition() { return definition; }

    public void setDefinition(PlotDefinition definition) {
        this.definition = definition;
    }

    public PlotFlags getFlags() { return flags; }

    public void setFlags( PlotFlags flags ) { this.flags = flags.copy(); }

    public void setFlag(PlotFlags.Flag flag, PlotFlags.Extent ext) {
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

    @Override
    public Text getFormattedInfo() {

        Town parent = getParent().get();

        String nationName = "None";
        if ( parent.getParent().isPresent() ) nationName = parent.getParent().get().getName();

        return Text.builder()
                .append(Text.of(TownsConfig.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(TownsConfig.TERTIARY_COLOR, TextStyles.BOLD, name, TextStyles.RESET) )
                .append(Text.of(TextColors.RESET, TownsConfig.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET))
                .append( Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, "Town: ", TextStyles.RESET, parent.getColor(), parent.getName(), TownsConfig.PRIMARY_COLOR, " ( ", TownsConfig.TEXT_COLOR, nationName, TownsConfig.PRIMARY_COLOR, " )\n" ))
                .append( Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, "Flags: ", TextStyles.RESET, flags.formattedSingleLine(), "\n") )
                .build();
    }

    public void remove() {
        // TODO: When a plot is claimed and unclaimed, the plot is still stored in the database
        PlotManager.getInstance().remove(this);
    }
}
