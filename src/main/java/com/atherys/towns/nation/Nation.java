package com.atherys.towns.nation;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.base.BaseAreaObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.utils.Serialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

public class Nation extends AreaObject<Town> {

    public static class Builder {

        Nation nation;

        Builder ( UUID uuid ) {
            nation = new Nation(uuid);
        }

        public Nation.Builder allies ( Nation... allies ) {
            nation.addAllies ( allies );
            return this;
        }

        public Nation.Builder enemies ( Nation... enemies ) {
            nation.addEnemies ( enemies );
            return this;
        }

        public Nation.Builder towns ( Town... towns ) {
            nation.addTowns(towns);
            return this;
        }

        public Nation.Builder townsDb ( Town... towns ) {
            for ( Town t : towns ) {
                nation.addTown(t, t.status());
            }
            return this;
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

    private List<Nation> allies;
    private List<Nation> enemies;

    public Nation(UUID uuid) {
        super(uuid);
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
    }

    private Nation (UUID uuid, String name, String description, List<Town> towns, List<Nation> allies, List<Nation> enemies) {
        super(uuid);
        this.name = name;
        this.description = description;
        this.allies = allies;
        this.enemies = enemies;
        AtherysTowns.getInstance().getNationManager().add(this);
    }

    public Nation(String name, Town capital) {
        super(UUID.randomUUID());
        this.name = name;
        this.description = "";
        addTown(capital);
        setCapital(capital);
        allies = new LinkedList<>();
        enemies = new LinkedList<>();

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

    public Optional<Town> capital() {
        for ( Town t : contents ) {
            if ( t.status().equals(TownStatus.CAPITAL) ) return Optional.of(t);
        }
        return Optional.empty();
    }

    public Nation setCapital  ( Town newCapital ) {
        if ( !contents.contains(newCapital) ) return this;
        if ( capital().isPresent() ) {
            capital().get().setStatus(TownStatus.TOWN);
        }
        newCapital.setStatus(TownStatus.CAPITAL);
        newCapital.mayor().ifPresent(resident -> resident.setNationRank(NationRank.LEADER));
        return this;
    }

    public Nation addTown ( Town town, TownStatus status ) {
        if ( contents.contains(town) ) return this;
        town.setNation(this);
        town.setStatus(status);
        for ( Resident r : town.residents() ) {
            r.setNationRank (NationRank.RESIDENT);
        }
        contents.add(town);
        return this;
    }

    public Nation __addTown ( Town town, TownStatus status ) {
        if ( contents.contains(town) ) return this;
        town.setNation(this);
        town.setStatus(status);
        contents.add(town);
        return this;
    }

    public Nation addTown ( Town town ) {
        if ( contents.contains(town) ) return this;
        town.setNation(this);
        town.setStatus(TownStatus.TOWN);
        for ( Resident r : town.residents() ) {
            r.setNationRank (NationRank.RESIDENT);
        }
        contents.add(town);
        return this;
    }

    public Nation addTowns ( Town... towns ) {
        for ( Town t : towns ) {
            addTown(t);
        }
        return this;
    }

    public boolean hasTown ( Town town ) {
        return contents.contains(town);
    }

    @Override
    public void setName ( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String name() { return name; }

    @Override
    public Optional<? extends AreaObject<? extends BaseAreaObject>> getParent() {
        return null;
    }

    public List<Nation> getAllies() { return allies; }
    public boolean isAlliedWith ( Nation nation ) {
        if ( allies.contains(nation) ) return true;
        return false;
    }

    public List<Nation> getEnemies() { return enemies; }
    public boolean isEnemiesWith ( Nation nation ) {
        if ( enemies.contains(nation) ) return true;
        return false;
    }

    public String description() {
        return description;
    }

    public Nation setDescription(String description) {
        this.description = description;
        return this;
    }

    public Text formatInfo() {

        String leaderName = Settings.NON_PLAYER_CHARACTER_NAME;
        if ( capital().isPresent() && capital().get().mayor().isPresent() ) {
            leaderName = capital().get().mayor().get().getName();
        }

        return Text.builder()
                .append(Text.of(Settings.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(color(), TextStyles.BOLD, getName(), TextStyles.RESET ) )
                .append(Text.of(TextColors.RESET, Settings.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Description: ", TextStyles.RESET, Settings.TEXT_COLOR, description, "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Bank: ", TextStyles.RESET, Settings.TEXT_COLOR, getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, leaderTitle, ": ", TextStyles.RESET, Settings.TEXT_COLOR, leaderName + "\n" ) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Allies[", Settings.TEXT_COLOR, allies.size(), Settings.PRIMARY_COLOR ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedAllies(), "\n" ) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Enemies[", Settings.TEXT_COLOR, enemies.size(), Settings.PRIMARY_COLOR ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedEnemies(), "\n" ) )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Towns[", Settings.TEXT_COLOR, contents.size(), Settings.PRIMARY_COLOR ,"]: ", TextStyles.RESET, TextColors.RESET, getFormattedTowns() ) )
                .build();
    }

    private Text getFormattedTowns() {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for ( Town t : contents ) {
            Text resText = Text.builder()
                    .append(Text.of(t.getName()))
                    .onHover(TextActions.showText(Text.of(Settings.SECONDARY_COLOR, "Click for more info!")) )
                    .onClick(TextActions.runCommand("/town info " + t.getName() ) )
                    .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    private Text getFormattedEnemies () {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for ( Nation n : enemies ) {
            Text resText = Text.builder()
                    .append(Text.of(n.getName()))
                    .onHover(TextActions.showText(Text.of(Settings.SECONDARY_COLOR, "Click for more info!")) )
                    .onClick(TextActions.runCommand("/nation " + n.getName() ) )
                    .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    private Text getFormattedAllies () {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for ( Nation n : allies ) {
            Text resText = Text.builder()
                    .append(Text.of(n.getName()))
                    .onHover(TextActions.showText(Text.of(Settings.SECONDARY_COLOR, "Click for more info!")) )
                    .onClick(TextActions.runCommand("/nation " + n.getName() ) )
                    .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    public TextColor color() {
        return color;
    }

    public void removeTown(Town town) {
        contents.remove(town);
    }

    public Optional<Resident> searchResident(UUID uuid) {
        for ( Town t : contents ) {
            Optional<Resident> res = t.searchResident ( uuid );
            if ( res.isPresent() ) return res;
        }
        return Optional.empty();
    }

    public void addAlly ( Nation n ) {
        this.allies.add(n);
        n.allies.add(this);
        TownMessage.informAll(Text.of("The nations of ", this.formattedName(), " and ", n.formattedName(), " are now allies!"));
    }

    public void removeAlly ( Nation n ) {
        this.allies.remove(n);
        n.allies.remove(this);
        TownMessage.warnAll(Text.of("The nations of ", this.formattedName(), " and ", n.formattedName(), " are no longer allied!"));
    }

    private void addAllies(Nation... allies) {
        for ( Nation n : allies ) addAlly(n);
    }

    public void addEnemy ( Nation n ) {
        this.enemies.add(n);
        n.enemies.add(this);
        TownMessage.warnAll(Text.of("The nations of ", this.formattedName(), " and ", n.formattedName(), " are now enemies!"));
    }

    public void removeEnemy ( Nation n ) {
        this.enemies.remove(n);
        n.enemies.remove(this);
        TownMessage.informAll(Text.of("The nations of ", this.formattedName(), " and ", n.formattedName(), " are no longer enemies!"));
    }

    private void addEnemies(Nation... enemies) {
        for ( Nation n : enemies ) addEnemy(n);
    }

    private Text formattedName() {
        return Text.of(color, name).toBuilder().onHover(TextActions.showText(this.formatInfo())).build();
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setLeaderTitle(String leaderTitle) {
        this.leaderTitle = leaderTitle;
    }

    public String leaderTitle() {
        return leaderTitle;
    }

    public void informResidents(String s) {
        for ( Town t : super.contents ) {
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
        map.put(NationManager.Table.ALLIES_UUIDS, Serialize.nationList(allies));
        map.put(NationManager.Table.ENEMIES_UUIDS, Serialize.nationList(enemies));
        return map;
    }
}
