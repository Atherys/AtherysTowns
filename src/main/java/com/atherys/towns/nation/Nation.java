package com.atherys.towns.nation;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.utils.Serialize;
import math.geom2d.Point2D;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class Nation extends AreaObject<Nation> { // what is the parent of a nation?

    public static class Builder {

        Nation nation;

        Builder ( UUID uuid ) {
            nation = new Nation(uuid);
        }

        public Nation.Builder name ( String name ) {
            nation.setName(name);
            return this;
        }

        public Nation.Builder color(TextColor color) {
            nation.setColor(color);
            return this;
        }

        public Nation.Builder description ( String description ) {
            nation.setDescription(description);
            return this;
        }

        public Nation.Builder leaderTitle(String title) {
            nation.setLeaderTitle(title);
            return this;
        }

        public Nation build() {
            AtherysTowns.getInstance().getNationManager().add(nation);
            return nation;
        }
    }

    private String name;
    private String leaderTitle = "Leader";
    private TextColor color = TextColors.WHITE;
    private String description = "";

    // allies and enemies are bullshit. Let's not do them.

    public Nation(UUID uuid) {
        super(uuid);
    }

    private Nation (UUID uuid, String name, String description, List<Town> towns, List<Nation> allies, List<Nation> enemies) {
        super(uuid);
        this.name = name;
        this.description = description;
        AtherysTowns.getInstance().getNationManager().add(this);
    }

    public Nation(String name, Town capital) {
        super(UUID.randomUUID());
        this.name = name;
        this.description = "";
        setCapital(capital);

        AtherysTowns.getInstance().getNationManager().add(this);
        AtherysTowns.getInstance().getNationManager().save(this);
    }

    public static Nation create (UUID uuid, String name, String description, List<Town> towns, List<Nation> allies, List<Nation> enemies) {
        return new Nation( uuid, name, description, towns, allies, enemies );
    }

    public static Nation create( String name, Town capital ) {
        return new Nation( name, capital );
    }

    public static Nation.Builder builder() {
        return new Nation.Builder(UUID.randomUUID());
    }

    public static Nation.Builder fromUUID ( UUID uuid ) {
        return new Nation.Builder(uuid);
    }

    public Optional<Town> getCapital() {
        for ( Town t : getTowns() ) {
            if ( t.getStatus().equals(TownStatus.CAPITAL) ) return Optional.of(t);
        }
        return Optional.empty();
    }

    public Nation setCapital  ( Town newCapital ) {
        Optional<Town> capital = getCapital();
        capital.ifPresent(town -> town.setStatus(TownStatus.TOWN));
        newCapital.setStatus(TownStatus.CAPITAL);
        newCapital.getMayor().ifPresent(resident -> resident.setNationRank(NationRank.LEADER));
        return this;
    }

    public boolean hasTown ( Town town ) {
        return getTowns().contains(town);
    }

    @Override
    public void setName ( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean contains(World w, double x, double y) {
        for ( Town t : getTowns() ) {
            if ( t.contains(w, x, y) ) return true;
        }
        return false;
    }

    @Override
    public boolean contains(World w, Point2D point) {
        for ( Town t : getTowns() ) {
            if ( t.contains(w, point) ) return true;
        }
        return false;
    }

    @Override
    public boolean contains(Location<World> loc) {
        for ( Town t : getTowns() ) {
            if ( t.contains(loc) ) return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public Nation setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<Resident> getResidents() {
        return AtherysTowns.getInstance().getResidentManager().getByNation(this);
    }

    public List<Town> getTowns() {
        return AtherysTowns.getInstance().getTownManager().getByNation(this);
    }

    public Text formatInfo() {

        String leaderName = Settings.NON_PLAYER_CHARACTER_NAME;

        Optional<Town> capital = getCapital();
        if ( capital.isPresent() ) {
            Optional<Resident> leader = capital.get().getMayor();
            if ( leader.isPresent() ) leaderName = leader.get().getName();
        }

        List<Town> towns = getTowns();

        return Text.builder()
                .append(Text.of(Settings.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(color, TextStyles.BOLD, getName(), TextStyles.RESET ) )
                .append(Text.of(TextColors.RESET, Settings.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Description: ", TextStyles.RESET, Settings.TEXT_COLOR, description, "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Bank: ", TextStyles.RESET, Settings.TEXT_COLOR, getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, leaderTitle, ": ", TextStyles.RESET, Settings.TEXT_COLOR, leaderName + "\n" ) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Towns[", Settings.TEXT_COLOR, towns.size(), Settings.PRIMARY_COLOR ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedTowns(towns) ) )
                .build();
    }

    private Text getFormattedTowns( List<Town> towns ) {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for ( Town t : towns ) {
            Text resText = Text.builder()
                    .append(Text.of(t.getName()))
                    .onHover(TextActions.showText(Text.of(Settings.SECONDARY_COLOR, "Click for more info!")) )
                    .onClick(TextActions.runCommand("/town info " + t.getName() ) )
                    .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    public TextColor getColor() {
        return color;
    }

    private Text getFormattedName() {
        return Text.of(color, name).toBuilder().onHover(TextActions.showText(this.formatInfo())).build();
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setLeaderTitle(String leaderTitle) {
        this.leaderTitle = leaderTitle;
    }

    public String getLeaderTitle() {
        return leaderTitle;
    }

    public void informResidents ( String s ) {
        for ( Town t : getTowns() ) {
            t.informResidents(Text.of(s));
        }
    }

    @Override
    public Text getFormattedInfo() {
        return formatInfo();
    }

    @Override
    public Map<NationManager.Table, Object> toDatabaseStorable() {
        Map<NationManager.Table,Object> map = new HashMap<>();
        map.put(NationManager.Table.UUID, getUUID().toString());
        map.put(NationManager.Table.NAME, this.name);
        map.put(NationManager.Table.LEADER_TITLE, leaderTitle);
        map.put(NationManager.Table.COLOR, Serialize.color(color));
        map.put(NationManager.Table.DESCRIPTION, description);
        map.put(NationManager.Table.TAX, 0.0d);
        return map;
    }
}
