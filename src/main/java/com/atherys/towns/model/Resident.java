package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import org.spongepowered.api.entity.living.player.User;

import java.util.Optional;
import java.util.UUID;

public class Resident implements DBObject {

    private UUID uuid;

    private Town town;

    public Resident(UUID uuid) {
        this.uuid = uuid;
    }

    public <T extends User> Resident(T user) {
        this.uuid = user.getUniqueId();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public Optional<Town> getTown() {
        return Optional.ofNullable(town);
    }

    public void setTown(Town town) {
        this.town = town;
    }
}
