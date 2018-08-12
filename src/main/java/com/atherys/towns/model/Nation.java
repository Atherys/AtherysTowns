package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Nation implements DBObject {

    private UUID uuid;

    private Text name;
    private Text description;

    private Town capital;
    private Set<Town> towns = new HashSet<>();

    public Nation(Text name, Text description, Town capital) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.capital = capital;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public Town getCapital() {
        return capital;
    }

    public Set<Town> getTowns() {
        return towns;
    }
}
