package com.atherys.towns.town;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.resident.Resident;
//import com.atherys.towns.utils.old.Question;
import com.flowpowered.math.vector.Vector3d;
import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class Town extends AreaObject<Nation> {

    public static class Builder {

        Town town;

        Builder ( Town town ) {
            this.town = town;
        }

        Builder ( UUID uuid ) {
            town = new Town(uuid);
        }

        Builder () {
            town = new Town(UUID.randomUUID());
        }

        public Town.Builder nation ( Nation nation ) {
            if ( nation == null ) return this;
            town.setParent(nation);
            return this;
        }

        public Town.Builder status ( TownStatus status ) {
            town.setStatus( status );
            return this;
        }

        public Town.Builder flags ( PlotFlags flags ) {
            town.setFlags ( flags );
            return this;
        }

        public Town.Builder maxSize ( int max ) {
            town.setMaxSize(max);
            return this;
        }

        public Town.Builder spawn ( Location spawn ) {
            town.setSpawn(spawn);
            return this;
        }

        public Town.Builder name ( String name ) {
            town.setName(name);
            return this;
        }

        public Town.Builder motd ( String motd ) {
            town.setMOTD( motd );
            return this;
        }

        public Town.Builder description ( String desc ) {
            town.setDescription(desc);
            return this;
        }

        public Town.Builder color ( TextColor color ) {
            town.setColor(color);
            return this;
        }

        public Town build() {
            TownManager.getInstance().add(town);
            return town;
        }

    }

    private TownStatus status = TownStatus.NONE;

    private PlotFlags townFlags;
    private int maxSize;
    private Location<World> spawn;

    private String name;
    private TextColor color = TownsConfig.SECONDARY_COLOR;
    private String motd = "Message of the day.";
    private String description = "Town description.";

    private Town( UUID uuid ) {
        super(uuid);
        this.townFlags = PlotFlags.regular();
        this.motd = "";
        this.description = "";
    }

    private Town ( PlotDefinition define, Resident mayor ) {
        super(UUID.randomUUID());
        this.spawn = mayor.getPlayer().get().getLocation();
        Plot homePlot = Plot.create(define, this, "Home");
        this.townFlags = homePlot.getFlags().copy();
        claimPlot ( homePlot );
        this.setParent( null );
        this.status = TownStatus.NONE;
        mayor.setTown( this, TownsConfig.TOWN_LEADER_RANK );
        TownManager.getInstance().add(this);
    }

    public static Town create( PlotDefinition definition, Resident mayor, String name, int maxAllowedPlots ) {
        Town t = new Town(definition, mayor);
        t.setName(name);
        t.setMaxSize(maxAllowedPlots);
        TownMessage.informAll(Text.of("A new town ( " + name + " ) has been created!"));
        TownManager.getInstance().saveOne(t);
        return t;
    }

    public static Town.Builder fromUUID ( UUID uuid ) {
        return new Town.Builder( uuid );
    }

    public static Town.Builder builder() {
        return new Town.Builder();
    }

    public void setFlags(PlotFlags flags) {
        this.townFlags = flags;
    }

    public void informResidents ( Text message ) {
        for ( Resident res : getResidents() ) {
            if ( res.getPlayer().isPresent() ) {
                TownMessage.inform(res.getPlayer().get(), message);
            }
        }
    }

    public void warnResidents ( Text message ) {
        for ( Resident res : getResidents() ) {
            if ( res.getPlayer().isPresent() ) {
                TownMessage.warn(res.getPlayer().get(), message);
            }
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public Location<World> getSpawn() {
        return spawn;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public PlotFlags getTownFlags() {
        return townFlags;
    }

    public void setFlag ( PlotFlags.Flag flag, PlotFlags.Extent extents ) {
        this.townFlags.set(flag, extents);
        for ( Plot p : getPlots() ) {
            p.getFlags().set(flag, extents);
        }
    }

    @Override
    public boolean contains(World w, double x, double y) {
        for ( Plot p : getPlots() ) {
            if ( p.contains(w, x, y) ) return true;
        }
        return false;
    }

    @Override
    public boolean contains(World w, Point2D point) {
        return contains(w, point.x(), point.y());
    }

    @Override
    public boolean contains(Location<World> loc) {
        return contains(loc.getExtent(), loc.getX(), loc.getY());
    }

    public void setNation ( Nation nation ) {
        if ( nation != null ) {
            TownMessage.informAll( Text.of("The town of " + name + " has joined the nation of " + nation.getName()));
        } else {
            TownMessage.informAll( Text.of("The town of " + name + " is now nationless!") );
        }
        this.setParent( nation );
    }

    public TownStatus getStatus() { return status; }

    public void setStatus ( TownStatus status ) {
        this.status = status;
    }

    @Override
    public Text getFormattedInfo() {

        String nationName = "None";
        if ( getParent().isPresent() ) {
            nationName = getParent().get().getName();
        }

        long plotSize = 0;
        for ( Plot p : getPlots() ) {
            plotSize += p.getDefinition().area();
        }

        String mayorName = TownsConfig.NON_PLAYER_CHARACTER_NAME;
        Optional<Resident> mayor = getMayor();
        if ( mayor.isPresent() ) mayorName = mayor.get().getName();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        Text pvp = Text.of( TextStyles.BOLD, TextColors.GREEN, "( PvP On )" );
        if ( townFlags.get(PlotFlags.Flag.PVP) == PlotFlags.Extent.NONE ) pvp = Text.of( TextStyles.BOLD, TextColors.RED, "( PvP Off )" );

        TextColor decoration = TownsConfig.DECORATION_COLOR;
        TextColor primary = TownsConfig.PRIMARY_COLOR;
        TextColor textColor = TownsConfig.TEXT_COLOR;

        return Text.builder()
                .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(color, TextStyles.BOLD, name, TextStyles.RESET, " ", pvp) )
                .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "MOTD: ", TextStyles.RESET,         textColor, motd, "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Description: ", TextStyles.RESET,  textColor, description, "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET,         textColor, super.getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Flags: ", TextStyles.RESET,        Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET).toBuilder().onHover(TextActions.showText(townFlags.formatted())).build(), "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Nation: ", TextStyles.RESET,       textColor, nationName + "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Size: ", TextStyles.RESET,         textColor, formatter.format( plotSize ), "/", formatter.format(maxSize), TextStyles.ITALIC, TextColors.DARK_GRAY, " ( ", formatter.format(maxSize - plotSize), " remaining )", "\n" ) )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Mayor: ", TextStyles.RESET,        textColor, mayorName + "\n" ) )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Residents[",                       textColor, getResidents().size(), primary ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedResidents() ) )
                .build();
    }

    private Text getFormattedResidents() {
        List<Resident> residentsByLastOnline = sortResidentsByDate(getResidents());
        Text.Builder residentsBuilder = Text.builder();
        Text separator = Text.of(", ");

        TextColor primary = TownsConfig.PRIMARY_COLOR;
        TextColor textColor = TownsConfig.TEXT_COLOR;

        int iterations = 0;
        for ( Resident r : residentsByLastOnline ) {
            if ( iterations == 25 ) break;
            TownRank tr = r.getTownRank();
            Text resText = Text.builder()
                    .append(Text.of(r.getName()))
                    .onHover(TextActions.showText(Text.of(
                            primary, TextStyles.BOLD, "Rank: ", TextStyles.RESET,        textColor, tr.getName(), "\n",
                            primary, TextStyles.BOLD, "Last Online: ", TextStyles.RESET, textColor, r.getFormattedLastOnlineDate()

                    ) ) )
                    .onClick(TextActions.runCommand("/res " + r.getName()) )
                    .build();

            residentsBuilder.append(resText, separator);
            iterations++;
        }
        return residentsBuilder.build();
    }

    private List<Resident> sortResidentsByDate(List<Resident> list) {
        Resident res;
        for ( int i = 0; i < list.size(); i++ ) {
            for ( int j = 0; j<list.size(); j++ ) {
                if ( list.get(i).getLastOnlineSeconds() > list.get(j).getLastOnlineSeconds() ) {
                    res = list.get(j);
                    list.set(j, list.get(i));
                    list.set(i, res);
                }
            }
        }
        return list;
    }

    public void claimPlot(Plot p) {
        p.setParent(this);
        p.setFlags(townFlags);
    }

    public void unclaimPlot(Plot p) {
        p.setParent(null);
        p.remove();
    }

    public void setMayor(Resident newMayor) {
        if ( newMayor.getTown().isPresent() && newMayor.getTown().get().equals(this) ) {
            getMayor().ifPresent( resident -> resident.setTownRank(TownRanks.CO_MAYOR) );
            newMayor.setTownRank(TownRanks.MAYOR);
        }
    }

    public Optional<Resident> getMayor() {
        for ( Resident r : getResidents() ) {
            if ( r.getTownRank().equals(TownRanks.MAYOR) ) return Optional.of(r);
        }
        return Optional.empty();
    }

    public Optional<Resident> getResident ( UUID uuid ) {
        for ( Resident resident : getResidents() ) {
            if ( resident.getUUID().equals(uuid) ) {
                return Optional.of( resident );
            }
        }
        return Optional.empty();
    }

    public String getMOTD() {
        return motd;
    }

    public void setMOTD(String MOTD) {
        this.motd = MOTD;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TextColor getColor() {
        return color;
    }

    public Town setColor(TextColor color) {
        this.color = color;
        return this;
    }

    public double getArea() {
        double area = 0;
        for ( Plot p : getPlots() ) {
            area += p.getDefinition().area();
        }
        return area;
    }

    public void ruin() {

        getPlots().forEach(Plot::remove); // remove all plots.
        getResidents().forEach( resident ->
            resident.setTown(null, TownRanks.NONE)
        );
        TownManager.getInstance().remove(this); // remove town from town manager. Doing this remove any reference from the object, leaving it to to the whims of the GC
        TownMessage.warnAll(Text.of("The town of " + this.name + " is no more."));
    }

    public List<Resident> getResidents() {
        return ResidentManager.getInstance().getByTown(this);
    }

    public List<Plot> getPlots() {
        return PlotManager.getInstance().getByParent(this);
    }

    public void showBorders(Player p) {
        List<LineSegment2D> borderedEdges = new LinkedList<>();
        for ( Plot plot : this.getPlots() ) {
            for ( LineSegment2D edge : plot.getDefinition().edges() ) {
                if ( !doesEdgeAlmostEqualAnyOther(edge, borderedEdges) ) {
                    for (int i = 0; i <= edge.length(); i += 1) {
                        Point2D twoD = interpolationByDistance(edge, i);
                        Vector3d loc = new Vector3d(twoD.x(), p.getLocation().getExtent().getHighestYAt( (int) twoD.x(), (int) twoD.y()), twoD.y());
                        p.spawnParticles(ParticleEffect.builder()
                                .velocity(Vector3d.from(0, 0.08, 0))
                                .type(ParticleTypes.BARRIER)
                                .quantity(1)
                                .build(), loc);
                    }
                    borderedEdges.add(edge);
                }
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

    private static boolean doesEdgeAlmostEqualAnyOther ( LineSegment2D edge, List<LineSegment2D> list ) {
        for ( LineSegment2D e : list ) {
            if ( edge.almostEquals(e, 1.0d) ) return true;
        }
        return false;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}
