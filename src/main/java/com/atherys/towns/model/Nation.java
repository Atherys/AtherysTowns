package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.atherys.towns.model.permission.ResidentRank;
import com.atherys.towns.model.permission.RankDefinitionStorage;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.text.format.TextColor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Entity(value = "nations", noClassnameStored = true)
public class Nation implements DBObject, RankDefinitionStorage {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private TextColor color;

    private Town capital;

    private Set<Town> towns = new HashSet<>();

    private Set<ResidentRank> residentRanks = new TreeSet<>();

    private Nation() {}

    public Nation(Town capital) {
        this.capital = capital;
        towns.add(capital);
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

    public TextColor getColor() {
        return color;
    }

    public Town getCapital() {
        return capital;
    }

    public Set<Town> getTowns() {
        return towns;
    }

    public boolean addTown(Town town) {
        return towns.add(town);
    }

    public boolean removeTown(Town o) {
        return towns.remove(o);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type getPermissionContext() {
        return Type.NATION;
    }

    @Override
    public Set<ResidentRank> getDefinedResidentRanks() {
        return residentRanks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nation nation = (Nation) o;
        return Objects.equals(uuid, nation.uuid) &&
                Objects.equals(name, nation.name) &&
                Objects.equals(description, nation.description) &&
                Objects.equals(color, nation.color) &&
                Objects.equals(towns, nation.towns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, description, color, towns);
    }

}
