package com.atherys.towns.model;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
public class Nation implements SpongeIdentifiable {

    @Id
    private UUID uuid;

    @Convert(converter = TextConverter.class)
    private Text name;

    @Convert(converter = TextConverter.class)
    private Text description;

    @OneToMany
    private Set<Town> towns;

    @OneToOne
    private Resident leader;

    @OneToOne
    private Town capital;

    public Nation() {
    }

    public Nation(UUID uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Set<Town> getTowns() {
        return towns;
    }

    public void setTowns(Set<Town> towns) {
        this.towns = towns;
    }

    public Resident getLeader() {
        return leader;
    }

    public void setLeader(Resident leader) {
        this.leader = leader;
    }

    public Town getCapital() {
        return capital;
    }

    public void setCapital(Town capital) {
        this.capital = capital;
    }
}
