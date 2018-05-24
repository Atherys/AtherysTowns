package com.atherys.towns.nation;

import com.atherys.core.views.Viewable;
import com.atherys.towns.api.AbstractAreaObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.managers.TownManager;
import com.atherys.towns.permissions.ranks.NationRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.town.TownStatus;
import com.atherys.towns.views.NationView;
import math.geom2d.Point2D;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Nation extends AbstractAreaObject<Nation> implements Viewable<NationView> {

    private String name;
    private String leaderTitle = "Leader";
    private TextColor color = TextColors.WHITE;
    private String description = "";
    private double tax = 0.0;

    // TODO: Allies & Enemies

    protected Nation(UUID uuid) {
        super(uuid);
    }

    private Nation(UUID uuid, String name, String description) {
        super(uuid);
        this.name = name;
        this.description = description;
        NationManager.getInstance().add(this);
    }

    private Nation(String name, Town capital) {
        super(UUID.randomUUID());
        this.name = name;
        this.description = "";
        capital.setNation(this);
        capital.setStatus(TownStatus.CAPITAL);
        capital.getMayor().ifPresent(resident -> resident.setNationRank(NationRanks.LEADER));

        NationManager.getInstance().add(this);
        NationManager.getInstance().save(this);
    }

    public static Nation create(UUID uuid, String name, String description) {
        return new Nation(uuid, name, description);
    }

    public static Nation create(String name, Town capital) {
        return new Nation(name, capital);
    }

    public static NationBuilder builder() {
        return new NationBuilder(UUID.randomUUID());
    }

    public static NationBuilder fromUUID(UUID uuid) {
        return new NationBuilder(uuid);
    }

    public Optional<Town> getCapital() {
        for (Town t : getTowns()) {
            if (t.getStatus().equals(TownStatus.CAPITAL)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Nation setCapital(Town newCapital) {
        Optional<Town> capital = getCapital();
        capital.ifPresent(town -> town.setStatus(TownStatus.TOWN));
        newCapital.setStatus(TownStatus.CAPITAL);
        newCapital.getMayor().ifPresent(resident -> resident.setNationRank(NationRanks.LEADER));
        return this;
    }

    public boolean hasTown(Town town) {
        return getTowns().contains(town);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean contains(World w, double x, double y) {
        for (Town t : getTowns()) {
            if (t.contains(w, x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(World w, Point2D point) {
        for (Town t : getTowns()) {
            if (t.contains(w, point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Location<World> loc) {
        for (Town t : getTowns()) {
            if (t.contains(loc)) {
                return true;
            }
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

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public String getLeaderTitle() {
        return leaderTitle;
    }

    public void setLeaderTitle(String leaderTitle) {
        this.leaderTitle = leaderTitle;
    }

    public void informResidents(Text s) {
        for (Town t : getTowns()) {
            t.informResidents(s);
        }
    }

    @Override
    public NationView createView() {
        return new NationView(this);
    }
}
