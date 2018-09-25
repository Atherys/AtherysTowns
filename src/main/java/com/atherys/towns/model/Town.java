package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import java.util.*;

@Entity(value = "towns", noClassnameStored = true)
public class Town implements DBObject {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private String motd;

    private TextColor color;

    private World world;

    private Set<Resident> residents = new HashSet<>();

    private Set<Plot> plots = new HashSet<>();

    private int maxArea;

    private Nation nation;

    private Town() {}

    public Town(Resident mayor, World world) {
        this.uuid = UUID.randomUUID();
        addResident(mayor);
        this.world = world;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMotd() {
        return motd;
    }

    public TextColor getColor() {
        return color;
    }

    public World getWorld() {
        return world;
    }

    public Set<Resident> getResidents() {
        return residents;
    }

    public Set<Plot> getPlots() {
        return plots;
    }

    public Optional<Nation> getNation() {
        return Optional.ofNullable(nation);
    }

    public int getMaxArea() {
        return maxArea;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public boolean addResident(Resident resident) {
        return residents.add(resident);
    }

    public boolean removeResident(Resident o) {
        return residents.remove(o);
    }

    public void setMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    public boolean addPlot(Plot plot) {
        return plots.add(plot);
    }

    public boolean removePlot(Plot o) {
        return plots.remove(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return Objects.equals(uuid, town.uuid) &&
                Objects.equals(name, town.name) &&
                Objects.equals(description, town.description) &&
                Objects.equals(motd, town.motd) &&
                Objects.equals(color, town.color) &&
                Objects.equals(world.getUniqueId(), town.world.getUniqueId()) &&
                Objects.equals(residents, town.residents) &&
                Objects.equals(plots, town.plots) &&
                Objects.equals(nation, town.nation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, description, motd, color, world, residents, plots, nation);
    }
}
