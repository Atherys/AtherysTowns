package com.atherys.towns.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.utils.Question;
import com.atherys.towns.utils.Serialize;
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

        public Town.Builder maxArea ( int max ) {
            town.setMaxArea(max);
            return this;
        }

        public Town.Builder spawn(Location spawn) {
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
            AtherysTowns.getInstance().getTownManager().add(town);
            return town;
        }

    }

    private Nation nation;
    private TownStatus status;

    private PlotFlags townFlags;
    private int maxArea;
    private Location<World> spawn;

    private String name;
    private TextColor color = Settings.SECONDARY_COLOR;
    private String motd = "Message of the day.";
    private String description = "Town description.";

    private Town( UUID uuid ) {
        super(uuid);
        this.townFlags = PlotFlags.regular();
        this.motd = "";
        this.description = "";
    }

    private Town (UUID uuid, Nation nation, TownStatus status, PlotFlags townFlags, List<Plot> plots, int maxSize, Location<org.spongepowered.api.world.World> spawn, List<Resident> residents, String name, String motd, String description) {
        super(uuid);
        this.nation = nation;
        this.status = status;
        this.townFlags = townFlags;
        this.maxArea = maxSize;
        this.spawn = spawn;
        this.name = name;
        this.motd = motd;
        this.description = description;
        AtherysTowns.getInstance().getTownManager().add(this);
    }

    private Town ( PlotDefinition define, Resident mayor ) {
        super(UUID.randomUUID());
        this.spawn = mayor.getPlayer().get().getLocation();
        mayor.setTownRank(TownRank.MAYOR);
        Plot homePlot = Plot.create(define, this, "Home");
        this.townFlags = homePlot.getFlags().copy();
        claimPlot(homePlot);
        this.nation = null;
        this.status = TownStatus.NONE;
        mayor.setTown(this, TownRank.MAYOR);
        AtherysTowns.getInstance().getTownManager().add(this);
    }

    public static Town create( PlotDefinition definition, Resident mayor, String name, int maxAllowedPlots ) {
        Town t = new Town(definition, mayor);
        t.setName(name);
        t.setMaxArea(maxAllowedPlots);
        TownMessage.informAll(Text.of("A new town ( " + name + " ) has been created!"));
        AtherysTowns.getInstance().getTownManager().save(t);
        return t;
    }

    public static Town create(UUID uuid, Nation nation, TownStatus status, PlotFlags townFlags, List<Plot> plots, int plotLimit, Location<org.spongepowered.api.world.World> spawn, List<Resident> residents, String name, String motd, String description) {
        return new Town(uuid, nation, status, townFlags, plots, plotLimit, spawn, residents, name, motd, description);
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
        return maxArea;
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

    public Town setMaxArea(int maxArea) {
        this.maxArea = maxArea;
        return this;
    }

    public PlotFlags getTownFlags() {
        return townFlags;
    }

    public Town setFlag ( PlotFlags.Flag flag, PlotFlags.Extent extents ) {
        this.townFlags.set(flag, extents);
        for ( Plot p : getPlots() ) {
            p.getFlags().set(flag, extents);
        }
        return this;
    }

    @Override
    public Optional<Nation> getParent() {
        return Optional.ofNullable(nation);
    }

    @Override
    public boolean contains(World w, double x, double y) {
        return false;
    }

    @Override
    public boolean contains(World w, Point2D point) {
        return false;
    }

    @Override
    public boolean contains(Location<World> loc) {
        return false;
    }

    public Town setNation ( Nation nation ) {
        if ( nation != null ) {
            TownMessage.informAll( Text.of("The town of " + name + " has joined the nation of " + nation.getName()));
        } else {
            TownMessage.informAll( Text.of("The town of " + name + " is now nationless!") );
        }
        this.nation = nation;
        return this;
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

        String mayorName = Settings.NON_PLAYER_CHARACTER_NAME;
        Optional<Resident> mayor = getMayor();
        if ( mayor.isPresent() ) mayorName = mayor.get().getName();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        Text pvp = Text.of( TextStyles.BOLD, TextColors.GREEN, "( PvP On )" );
        if ( townFlags.get(PlotFlags.Flag.PVP) == PlotFlags.Extent.NONE ) pvp = Text.of( TextStyles.BOLD, TextColors.RED, "( PvP Off )" );

        TextColor decoration = Settings.DECORATION_COLOR;
        TextColor primary = Settings.PRIMARY_COLOR;
        TextColor textColor = Settings.TEXT_COLOR;

        return Text.builder()
                .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(color, TextStyles.BOLD, name, TextStyles.RESET, " ", pvp) )
                .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "MOTD: ", TextStyles.RESET,         textColor, motd, "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Description: ", TextStyles.RESET,  textColor, description, "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET,         textColor, super.getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Flags: ", TextStyles.RESET,        Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET).toBuilder().onHover(TextActions.showText(townFlags.formatted())).build(), "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Nation: ", TextStyles.RESET,       textColor, nationName + "\n") )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Size: ", TextStyles.RESET,         textColor, formatter.format( plotSize ), "/", formatter.format(maxArea), TextStyles.ITALIC, TextColors.DARK_GRAY, " ( ", formatter.format(maxArea - plotSize), " remaining )", "\n" ) )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Mayor: ", TextStyles.RESET,        textColor, mayorName + "\n" ) )
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Residents[",                       textColor, getResidents().size(), primary ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedResidents() ) )
                .build();
    }

    private Text getFormattedResidents() {
        List<Resident> residentsByLastOnline = sortResidentsByDate(getResidents());
        Text.Builder residentsBuilder = Text.builder();
        Text separator = Text.of(", ");

        TextColor primary = Settings.PRIMARY_COLOR;
        TextColor textColor = Settings.TEXT_COLOR;

        int iterations = 0;
        for ( Resident r : residentsByLastOnline ) {
            if ( iterations == 25 ) break;
            TownRank tr = r.getTownRank();
            Text resText = Text.builder()
                    .append(Text.of(r.getName()))
                    .onHover(TextActions.showText(Text.of(
                            primary, TextStyles.BOLD, "Rank: ", TextStyles.RESET,        textColor, tr.formattedName(), "\n",
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

    public void inviteResident(Resident resident) {
        Optional<Player> p = resident.getPlayer();

        if ( p.isPresent() ) {
            Player player = p.get();
            Text question;
            if ( getParent().isPresent() ) {
                question = Text.of("You have been invited to the town of \n", name, " in \n", nation.getName() );
            } else {
                question = Text.of("You have been invited to the town of \n", name );
            }

            TextColor primary = Settings.PRIMARY_COLOR;
            TextColor secondary = Settings.SECONDARY_COLOR;

            Text view = Question.asViewButton(
                Question.asText(question, Question.Type.ACCEPT_REFUSE,
                    // yes
                    (commandSource -> {
                        if ( commandSource instanceof Player ) {
                            Optional<Resident> res = AtherysTowns.getInstance().getResidentManager().get(((Player) commandSource).getUniqueId());
                            if ( res.isPresent() ) {
                                Resident r = res.get();
                                if ( r.town().isPresent() ) {
                                    TownMessage.warn((Player) commandSource, Text.of("You cannot join a town while you are part of another! Please leave your current town first."));
                                    return;
                                }
                                r.setTown(this, TownRank.RESIDENT);
                            }
                        }
                    })
                    ,
                    // no
                    (commandSource -> this.warnResidents(Text.of( resident.getName(), " has refused to join the town.") ))
                )
                ,
                Text.of( "\n", TownMessage.MSG_PREFIX,  TextStyles.BOLD, primary, "[", secondary, "Click Here To View", primary, "]" )
            );

            TownMessage.inform(player, question, view);
        }
    }

    public void setMayor(Resident newMayor) {
        if ( newMayor.town().isPresent() && newMayor.town().get().equals(this) ) {
            getMayor().ifPresent( resident -> resident.setTownRank(TownRank.CO_MAYOR) );
            newMayor.setTownRank(TownRank.MAYOR);
        }
    }

    public Optional<Resident> getMayor() {
        for ( Resident r : getResidents() ) {
            if ( r.getTownRank().equals(TownRank.MAYOR) ) return Optional.of(r);
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
        getResidents().forEach(Resident::leaveTown); // force all residents to leave the town
        AtherysTowns.getInstance().getTownManager().remove(this); // remove town from town manager. Doing this remove any reference from the object, leaving it to to the whims of the GC

        TownMessage.warnAll(Text.of("The town of " + this.name + " is no more."));
    }

    public List<Resident> getResidents() {
        return AtherysTowns.getInstance().getResidentManager().getByTown(this);
    }

    public List<Plot> getPlots() {
        return AtherysTowns.getInstance().getPlotManager().getByParent(this);
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

    @Override
    public Map<TownManager.Table, Object> toDatabaseStorable() {
        Map<TownManager.Table,Object> map = new HashMap<>();

        String nation_uuid = "NULL";
        if ( getParent().isPresent() ) nation_uuid = getParent().get().getUUID().toString();

        map.put(TownManager.Table.UUID, getUUID().toString());
        map.put(TownManager.Table.STATUS, status.id() );
        map.put(TownManager.Table.NATION_UUID, nation_uuid);
        map.put(TownManager.Table.FLAGS, Serialize.plotFlags(townFlags).toString() );
        map.put(TownManager.Table.MAX_AREA, maxArea);
        map.put(TownManager.Table.SPAWN, Serialize.location(spawn).toString());
        map.put(TownManager.Table.NAME, name);
        map.put(TownManager.Table.COLOR, Serialize.color(color));
        map.put(TownManager.Table.MOTD, motd);
        map.put(TownManager.Table.DESCRIPTION, description);

        return map;
    }
}
