package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity(value = "residents", noClassnameStored = true)
public class Resident implements DBObject {

    @Id
    private UUID uuid;

    private LocalDateTime registered;

    private LocalDateTime lastOnline;

    private Town town;

    private Resident() {}

    public Resident(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public LocalDateTime getRegistered() {
        return registered;
    }

    public LocalDateTime getLastOnline() {
        return lastOnline;
    }

    public Optional<Town> getTown() {
        return Optional.ofNullable(town);
    }
}
