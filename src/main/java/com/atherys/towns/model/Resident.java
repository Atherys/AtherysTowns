package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.service.permission.Subject;

import java.time.LocalDateTime;
import java.util.Objects;
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

    public void setRegistered(LocalDateTime registered) {
        this.registered = registered;
    }

    public void setLastOnline(LocalDateTime lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resident resident = (Resident) o;
        return Objects.equals(uuid, resident.uuid) &&
                Objects.equals(registered, resident.registered) &&
                Objects.equals(lastOnline, resident.lastOnline) &&
                Objects.equals(town, resident.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, registered, lastOnline, town);
    }
}
