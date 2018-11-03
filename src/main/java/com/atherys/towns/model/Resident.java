package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.atherys.towns.PermsCache;
import com.atherys.towns.model.permission.ResidentRank;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity(value = "residents", noClassnameStored = true)
public class Resident implements DBObject {

    @Id
    private UUID uuid;

    private LocalDateTime registered;

    private LocalDateTime lastOnline;

    private Town town;

    private Set<ResidentRank> residentRanks = new HashSet<>();

    @Transient
    private transient PermsCache permsCache = new PermsCache();

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

    public Set<ResidentRank> getResidentRanks() {
        return residentRanks;
    }

    public void setResidentRanks(Set<ResidentRank> residentRanks) {
        this.residentRanks = residentRanks;
    }

    public PermsCache getPermsCache() {
        return permsCache;
    }

    public Optional<Player> getPlayer() {
        return Sponge.getServer().getPlayer(uuid);
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
