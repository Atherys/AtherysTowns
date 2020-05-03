package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.core.db.converter.TransformConverter;
import com.atherys.towns.chat.TownMessageChannel;
import com.atherys.towns.persistence.converter.TextColorConverter;
import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class Town implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String name;

    @Convert(converter = TextConverter.class)
    private Text description;

    @Convert(converter = TextConverter.class)
    private Text motd;

    @Convert(converter = TextColorConverter.class)
    private TextColor color;

    @OneToOne
    private Resident leader;

    private String nation;

    private UUID world;

    @Convert(converter = TransformConverter.class)
    private Transform<World> spawn;

    @OneToMany(mappedBy = "town", fetch = FetchType.EAGER)
    private Set<Resident> residents = new HashSet<>();

    @OneToMany(mappedBy = "town", fetch = FetchType.EAGER)
    private Set<Plot> plots = new HashSet<>();

    @Transient
    private TownMessageChannel messageChannel;

    private int maxSize;

    private boolean freelyJoinable;

    private boolean pvpEnabled;

    private UUID bank;

    @Version
    private int version;

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
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

    public void addResident(Resident resident) {
        residents.add(resident);
    }

    public void removeResident(Resident resident) {
        residents.remove(resident);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Set<Plot> getPlots() {
        return plots;
    }

    public void setPlots(Set<Plot> plots) {
        this.plots = plots;
    }

    public void addPlot(Plot plot) {
        plots.add(plot);
    }

    public void removePlot(Plot plot) {
        plots.remove(plot);
    }

    public boolean isFreelyJoinable() {
        return freelyJoinable;
    }

    public void setFreelyJoinable(boolean freelyJoinable) {
        this.freelyJoinable = freelyJoinable;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }


    public TownMessageChannel getMessageChannel() {
        return messageChannel;
    }

    public void setMessageChannel(TownMessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public UUID getBank() {
        return bank;
    }

    public void setBank(UUID bank) {
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return maxSize == town.maxSize &&
                freelyJoinable == town.freelyJoinable &&
                pvpEnabled == town.pvpEnabled &&
                version == town.version &&
                Objects.equals(id, town.id) &&
                Objects.equals(name, town.name) &&
                Objects.equals(description, town.description) &&
                Objects.equals(motd, town.motd) &&
                Objects.equals(color, town.color) &&
                Objects.equals(leader, town.leader) &&
                Objects.equals(nation, town.nation) &&
                Objects.equals(world, town.world) &&
                Objects.equals(spawn, town.spawn) &&
                Objects.equals(residents, town.residents) &&
                Objects.equals(plots, town.plots) &&
                Objects.equals(messageChannel, town.messageChannel) &&
                Objects.equals(bank, town.bank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, motd, color, leader, nation, world, spawn, residents, plots, messageChannel, maxSize, freelyJoinable, pvpEnabled, bank, version);
    }
}
