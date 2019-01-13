package com.atherys.towns.model;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.api.Subject;
import com.atherys.towns.api.Actor;
import com.atherys.towns.persistence.converter.TextConverter;
import com.atherys.towns.persistence.converter.TransformConverter;
import org.hibernate.annotations.GenericGenerator;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Town implements SpongeIdentifiable, Subject<Nation>, Actor {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    @Convert(converter = TextConverter.class)
    private Text name;

    @Convert(converter = TextConverter.class)
    private Text description;

    @Convert(converter = TextConverter.class)
    private Text motd;

    @OneToOne
    private Resident leader;

    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "nation_id")
    private Nation nation;

    private UUID world;

    @Convert(converter = TransformConverter.class)
    private Transform<World> spawn;

    @OneToMany(mappedBy = "town")
    private Set<Resident> residents = new HashSet<>();

    @OneToMany(mappedBy = "town")
    private Set<Plot> plots = new HashSet<>();

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

    public Text getMotd() {
        return motd;
    }

    public void setMotd(Text motd) {
        this.motd = motd;
    }

    public Resident getLeader() {
        return leader;
    }

    public void setLeader(Resident leader) {
        this.leader = leader;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public Transform<World> getSpawn() {
        return spawn;
    }

    public void setSpawn(Transform<World> spawn) {
        this.spawn = spawn;
    }

    public Set<Resident> getResidents() {
        return residents;
    }

    public void setResidents(Set<Resident> residents) {
        this.residents = residents;
    }

    public Set<Plot> getPlots() {
        return plots;
    }

    public void setPlots(Set<Plot> plots) {
        this.plots = plots;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Nation getParent() {
        return nation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return uuid.equals(town.uuid) &&
                name.equals(town.name) &&
                description.equals(town.description) &&
                motd.equals(town.motd) &&
                leader.equals(town.leader) &&
                nation.equals(town.nation) &&
                world.equals(town.world) &&
                spawn.equals(town.spawn) &&
                residents.equals(town.residents) &&
                plots.equals(town.plots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, description, motd, leader, nation, world, spawn, residents, plots);
    }
}
