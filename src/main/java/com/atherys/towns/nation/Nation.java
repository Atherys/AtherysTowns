package com.atherys.towns.nation;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.permissions.ranks.NationRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import math.geom2d.Point2D;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Nation extends AreaObject<Nation> {

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

        public Nation.Builder tax ( double tax ) {
            nation.setTax(tax);
            return this;
        }

        public Nation build() {
            NationManager.getInstance().add(nation);
            return nation;
        }
    }

    private String name;
    private String leaderTitle = "Leader";
    private TextColor color = TextColors.WHITE;
    private String description = "";
    private double tax = 0.0;

    // allies and enemies are bullshit. Let's not do them.

    public Nation(UUID uuid) {
        super(uuid);
    }

    private Nation (UUID uuid, String name, String description) {
        super(uuid);
        this.name = name;
        this.description = description;
        NationManager.getInstance().add(this);
    }

    public Nation(String name, Town capital) {
        super(UUID.randomUUID());
        this.name = name;
        this.description = "";
        capital.setNation(this);
        capital.setStatus(TownStatus.CAPITAL);
        capital.getMayor().ifPresent(resident -> resident.setNationRank(NationRanks.LEADER));

        NationManager.getInstance().add(this);
        NationManager.getInstance().saveOne(this);
    }

    public static Nation create (UUID uuid, String name, String description) {
        return new Nation( uuid, name, description);
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
        newCapital.getMayor().ifPresent(resident -> resident.setNationRank(NationRanks.LEADER));
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
        return ResidentManager.getInstance().getByNation(this);
    }

    public List<Town> getTowns() {
        return TownManager.getInstance().getByParent(this);
    }

    @Override
    public Text getFormattedInfo() {

        String leaderName = TownsConfig.NON_PLAYER_CHARACTER_NAME;

        Optional<Town> capital = getCapital();
        if ( capital.isPresent() ) {
            Optional<Resident> leader = capital.get().getMayor();
            if ( leader.isPresent() ) leaderName = leader.get().getName();
        }

        List<Town> towns = getTowns();

        return Text.builder()
                .append(Text.of(TownsConfig.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(color, TextStyles.BOLD, getName(), TextStyles.RESET ) )
                .append(Text.of(TextColors.RESET, TownsConfig.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET) )
                .append(Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, "Description: ", TextStyles.RESET, TownsConfig.TEXT_COLOR, description, "\n") )
                .append(Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, "Bank: ", TextStyles.RESET, TownsConfig.TEXT_COLOR, getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, leaderTitle, ": ", TextStyles.RESET, TownsConfig.TEXT_COLOR, leaderName + "\n" ) )
                .append(Text.of(TextColors.RESET, TownsConfig.PRIMARY_COLOR, TextStyles.BOLD, "Towns[", TownsConfig.TEXT_COLOR, towns.size(), TownsConfig.PRIMARY_COLOR ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedTowns(towns) ) )
                .build();
    }

    private Text getFormattedTowns( List<Town> towns ) {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for ( Town t : towns ) {
            Text resText = Text.builder()
                    .append(Text.of(t.getName()))
                    .onHover(TextActions.showText(Text.of(TownsConfig.SECONDARY_COLOR, "Click for more info!")) )
                    .onClick(TextActions.runCommand("/town info " + t.getName() ) )
                    .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public TextColor getColor() {
        return color;
    }

    private Text getFormattedName() {
        return Text.of(color, name).toBuilder().onHover(TextActions.showText(this.getFormattedInfo())).build();
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

    public void informResidents ( Text s ) {
        for ( Town t : getTowns() ) {
            t.informResidents(s);
        }
    }
}
