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

public class Town extends AreaObject<Plot> {

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
            nation.__addTown(town, town.status());
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

        public Town.Builder plots(List<Plot> plots) {
            for ( Plot p : plots ) {
                town.claimPlot(p);
            }
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

        public Town.Builder residents(List<Resident> residents) {
            town.addResidents((Resident[]) residents.toArray());
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

    private List<Resident> residents;

    private String name;
    private TextColor color = Settings.SECONDARY_COLOR;
    private String motd = "Message of the day.";
    private String description = "Town description.";

    private Town( UUID uuid ) {
        super(uuid);
        this.townFlags = PlotFlags.regular();
        this.residents = new LinkedList<>();
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
        this.residents = residents;
        this.name = name;
        this.motd = motd;
        this.description = description;
        AtherysTowns.getInstance().getTownManager().add(this);
    }

    private Town ( PlotDefinition define, Resident mayor ) {
        super(UUID.randomUUID());
        this.spawn = mayor.getPlayer().get().getLocation();
        this.residents = new LinkedList<>();
        this.residents.add(mayor);
        mayor.setTownRank(TownRank.MAYOR);
        Plot homePlot = Plot.create(define, this, "Home");
        addPlot(homePlot);
        this.townFlags = homePlot.flags().copy();
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

    public Town.Builder toBuilder() {
        return new Town.Builder(this);
    }

    public void setFlags(PlotFlags flags) {
        this.townFlags = flags;
    }

    public void informResidents (Text message ) {
        for ( Resident res : residents ) {
            if ( res.getPlayer().isPresent() ) {
                TownMessage.inform(res.getPlayer().get(), message);
            }
        }
    }

    public void warnResidents(Text message) {
        for ( Resident res : residents ) {
            if ( res.getPlayer().isPresent() ) {
                TownMessage.warn(res.getPlayer().get(), message);
            }
        }
    }

    public void addResident (Resident resident, TownRank rank ) {
        if ( residents.contains(resident) ) return;
        resident.setTown(this, rank);
        residents.add( resident );
        informResidents(Text.of( resident.getName(), " is now online!"));
    }

    public void addResident (Resident resident ) {
        if ( residents.contains(resident) ) return;
        resident.setTown(this, TownRank.RESIDENT);
        residents.add( resident );
        informResidents(Text.of( resident.getName(), " has joined the town!"));
    }

    public void addResidents (Resident... residents ) {
        for ( Resident res : residents ) addResident(res, res.townRank());
    }

    public boolean removeResident ( Resident resident ) {
        if ( !residents.contains(resident) ) return false;
        resident.setTown(null, TownRank.NONE);
        residents.remove(resident);
        return true;
    }

    public boolean removeResidents ( Resident... residents ) {
        for ( Resident res : residents ) {
            if ( !removeResident(res) ) return false;
        }
        return true;
    }

    public List<Plot> plots() { return super.contents; }

    public List<Resident> residents () { return residents; }

    public int maxSize() {
        return maxArea;
    }

    public void setPlots(List<Plot> plots) {
        this.contents = plots;
    }

    public Location<World> spawn() {
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

    public PlotFlags flags() {
        return townFlags;
    }

    public Town setFlag ( PlotFlags.Flag flag, PlotFlags.Extent extents ) {
        this.townFlags.set(flag, extents);
        for ( Plot p : contents ) {
            p.flags().set(flag, extents);
        }
        return this;
    }

    @Override
    public Optional<Nation> getParent() {
        return Optional.ofNullable(nation);
    }

    public Town setNation ( Nation nation ) {
        if ( nation != null ) {
            TownMessage.informAll( Text.of("The town of " + name + " has joined the nation of " + nation.name()));
        } else {
            TownMessage.informAll( Text.of("The town of " + name + " is now nationless!") );
        }
        this.nation = nation;
        return this;
    }

    public TownStatus status() { return status; }

    public Town setStatus ( TownStatus status ) {
        this.status = status;
        return this;
    }

    @Override
    public Text getFormattedInfo() {

        String nationName = "None";
        if ( getParent().isPresent() ) {
            nationName = getParent().get().name();
        }

        long plotSize = 0;
        for ( Plot p : contents ) {
            plotSize += p.definition().area();
        }

        String mayorName = Settings.NON_PLAYER_CHARACTER_NAME;
        if ( this.mayor().isPresent() ) mayorName = this.mayor().get().getName();

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
                .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Residents[",                       textColor, residents.size(), primary ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedResidents() ) )
                .build();
    }

    public Text getFormattedResidents() {
        List<Resident> residentsByLastOnline = sortResidentsByDate(residents);
        Text.Builder residentsBuilder = Text.builder();
        Text separator = Text.of(", ");

        TextColor primary = Settings.PRIMARY_COLOR;
        TextColor textColor = Settings.TEXT_COLOR;

        int iterations = 0;
        for ( Resident r : residentsByLastOnline ) {
            if ( iterations == 25 ) break;
            TownRank tr = r.townRank();
            Text resText = Text.builder()
                    .append(Text.of(r.getName()))
                    .onHover(TextActions.showText(Text.of(
                            primary, TextStyles.BOLD, "Rank: ", TextStyles.RESET,        textColor, tr.formattedName(), "\n",
                            primary, TextStyles.BOLD, "Last Online: ", TextStyles.RESET, textColor, r.formattedLastOnlineDate()

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
                if ( list.get(i).lastOnlineSeconds() > list.get(j).lastOnlineSeconds() ) {
                    res = list.get(j);
                    list.set(j, list.get(i));
                    list.set(i, res);
                }
            }
        }
        return list;
    }

    public void addPlot (Plot p ) {
        this.contents.add(p);
        p.setParent(this);
    }

    public void claimPlot(Plot p) {
        this.contents.add(p);
        p.setParent(this);
        p.setFlags(townFlags);
    }

    public void unclaimPlot(Plot p) {
        this.contents.remove(p);
        p.remove();
    }

    public void inviteResident(Resident resident) {
        Optional<Player> p = resident.getPlayer();

        if ( p.isPresent() ) {
            Player player = p.get();
            Text question;
            if ( getParent().isPresent() ) {
                question = Text.of("You have been invited to the town of \n", name, " in \n", nation.name() );
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
                                this.addResident(r);
                            }
                        }
                    })
                    ,
                    // no
                    (commandSource -> {
                        this.warnResidents(Text.of( resident.getName(), " has refused to join the town.") );
                    })
                )
                ,
                Text.of( "\n", TownMessage.MSG_PREFIX,  TextStyles.BOLD, primary, "[", secondary, "Click Here To View", primary, "]" )
            );

            TownMessage.inform(player, question, view);
        }
    }

    public Town setMayor(Resident newMayor) {
        if ( newMayor.town().isPresent() && newMayor.town().get().equals(this) ) {
            if ( this.mayor().isPresent() ) {
                this.mayor().get().setTownRank(TownRank.CO_MAYOR);
            }
            newMayor.setTownRank(TownRank.MAYOR);
        }
        return this;
    }

    public Optional<Resident> mayor() {
        for ( Resident r : residents ) {
            if ( r.townRank().equals(TownRank.MAYOR) ) return Optional.of(r);
        }
        return Optional.empty();
    }

    public String motd() {
        return motd;
    }

    public void setMOTD(String MOTD) {
        this.motd = MOTD;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public TextColor color() {
        return color;
    }

    public Town setColor(TextColor color) {
        this.color = color;
        return this;
    }

    public double area() {
        double area = 0;
        for ( Plot p : contents ) {
            area += p.definition().area();
        }
        return area;
    }

    public Town sendMessage ( Resident res, Text message ) {
        if ( residents.contains(res) ) {
            for (Resident r : residents) {
                if (r.getPlayer().isPresent()) {
                    r.getPlayer().get().sendMessage( Text.of( TownMessage.TOWN_CHAT_PREFIX, TextColors.RESET, res.getName(),": ", Settings.TERTIARY_COLOR, message ) );
                }
            }
        }
        return this;
    }

    public Town sendMessage ( Resident res, Object... message ) {
        return sendMessage(res, Text.of(message));
    }

    public void ruin() {
        if ( getParent().isPresent() ) {
            nation.removeTown(this);
        }
        Iterator<Plot> iter = contents.iterator();
        while ( iter.hasNext() ) {
            Plot p = iter.next();
            unclaimPlot(p);
            p.remove();
            contents.remove(p);
        }
        for ( Resident r : residents ) {
            r.setTown(null, TownRank.NONE);
            residents.remove(r);
        }
        AtherysTowns.getInstance().getTownManager().remove(this);
        TownMessage.warnAll(Text.of("The town of " + this.name + " is no more."));
    }

    public void showBorders(Player p) {
        List<LineSegment2D> borderedEdges = new LinkedList<>();
        for ( Plot plot : contents ) {
            for ( LineSegment2D edge : plot.definition().edges() ) {
                if ( !doesEdgeAlmostEqualAnyOther(edge, borderedEdges) ) {
                    for (int i = 0; i <= edge.length(); i += 2) {
                        Point2D twoD = interpolationByDistance(edge, i);
                        Vector3d loc = new Vector3d(twoD.x(), p.getLocation().getBlockY(), twoD.y());
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
        double x = ratio*l.lastPoint().getX() + (1.0 - ratio)*l.firstPoint().getX();
        double y = ratio*l.lastPoint().getY() + (1.0 - ratio)*l.firstPoint().getY();
        return new Point2D( x, y );
    }

    private static boolean doesEdgeAlmostEqualAnyOther ( LineSegment2D edge, List<LineSegment2D> list ) {
        for ( LineSegment2D e : list ) {
            if ( edge.almostEquals(e, 1.0d) ) return true;
        }
        return false;
    }

    public Optional<Resident> searchResident(UUID uuid) {
        for ( Resident r : residents ) {
            if ( r.uuid().equals(uuid) ) return Optional.of(r);
        }
        return Optional.empty();
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
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
