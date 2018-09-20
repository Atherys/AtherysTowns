package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private Nation nation;

    private Town() {}

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
}
