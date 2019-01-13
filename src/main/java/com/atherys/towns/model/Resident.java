package com.atherys.towns.model;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.api.Actor;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Resident implements SpongeIdentifiable, Actor {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "town_id")
    private Town town;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "resident_friends",
            joinColumns = @JoinColumn(name = "resident_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Resident> friends = new HashSet<>();

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resident resident = (Resident) o;
        return uuid.equals(resident.uuid) &&
                name.equals(resident.name) &&
                town.equals(resident.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, town);
    }
}
