package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.text.format.TextColor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(value = "nations", noClassnameStored = true)
public class Nation implements DBObject {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private TextColor color;

    private Set<Town> towns = new HashSet<>();

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

    public Set<Town> getTowns() {
        return towns;
    }
}
