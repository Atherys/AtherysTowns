package com.atherys.towns.plot;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.BaseAreaObject;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.Serialize;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Plot implements BaseAreaObject {

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
            town.addPlot(plot);
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
            AtherysTowns.getInstance().getPlotManager().add(plot);
            return plot;
        }

    }

    private UUID uuid;

    private PlotDefinition definition;
    private Town town;
    private PlotFlags flags;

    private String name;

    private Plot ( UUID uuid ) {
        this.uuid = uuid;
    }

    private Plot (PlotDefinition definition, Town town, String name ) {
        this.definition = definition;
        this.town = town;
        flags = PlotFlags.regular();
        this.name = name;
        this.uuid = UUID.randomUUID();
        AtherysTowns.getInstance().getPlotManager().add(this);
        AtherysTowns.getInstance().getPlotManager().save(this);
    }

    public static Plot create ( PlotDefinition define, Town town, String name ) {
        return new Plot(define, town, name);
    }

    public Plot.Builder builder() { return new Plot.Builder(); }

    public static Plot.Builder fromUUID(UUID uuid) { return new Plot.Builder(uuid); }

    public boolean isResidentAllowedTo (Resident res, PlotFlags.Flag flag) { return this.flags.isAllowed(res, flag, this); }

    public PlotDefinition definition() { return definition; }

    public void setDefinition(PlotDefinition definition) {
        this.definition = definition;
    }

    public PlotFlags flags() { return flags; }

    public void setFlags( PlotFlags flags ) { this.flags = flags.copy(); }

    public void setFlag(PlotFlags.Flag flag, PlotFlags.Extent ext) {
        this.flags.set(flag, ext);
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
        double x = ratio*l.lastPoint().getX() + (1.0 - ratio)*l.firstPoint().getX();
        double y = ratio*l.lastPoint().getY() + (1.0 - ratio)*l.firstPoint().getY();
        return new Point2D( x, y );
    }

    @Override
    public UUID getUUID() { return uuid; }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

    @Override
    public Optional<Town> getParent() {
        return Optional.of(this.town);
    }

    public void setParent ( Town t ) {
        this.town = t;
    }

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

        String nationName = "None";
        if ( town.getParent().isPresent() ) nationName = town.getParent().get().name();

        return Text.builder()
                .append(Text.of(Settings.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(Settings.TERTIARY_COLOR, TextStyles.BOLD, name, TextStyles.RESET) )
                .append(Text.of(TextColors.RESET, Settings.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET))
                .append( Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Town: ", TextStyles.RESET, town.color(), town.getName(), Settings.PRIMARY_COLOR, " ( ", Settings.TEXT_COLOR, nationName, Settings.PRIMARY_COLOR, " )\n" ))
                .append( Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Flags: ", TextStyles.RESET, flags.formattedSingleLine(), "\n") )
                .build();
    }

    @Override
    public Map<PlotManager.Table, Object> toDatabaseStorable() {
        Map<PlotManager.Table,Object> map = new HashMap<>();
        map.put(PlotManager.Table.UUID,        this.uuid.toString());
        map.put(PlotManager.Table.TOWN_UUID,   getParent().get().getUUID().toString());
        map.put(PlotManager.Table.DEFINITION,  Serialize.definition(definition).toString());
        map.put(PlotManager.Table.FLAGS,       Serialize.plotFlags(flags).toString());
        map.put(PlotManager.Table.NAME,        name);
        return map;
    }

    public void remove() {
        AtherysTowns.getInstance().getPlotManager().remove(this);
    }
}
