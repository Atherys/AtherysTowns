package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.core.db.converter.TransformConverter;
import com.atherys.towns.persistence.converter.TextColorConverter;
import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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

    @OneToOne
    private Nation nation;

    private UUID world;

    @Convert(converter = TransformConverter.class)
    private Transform<World> spawn;

    @OneToMany(mappedBy = "town", fetch = FetchType.EAGER)
    private Set<Resident> residents = new HashSet<>();

    @OneToMany(mappedBy = "town", fetch = FetchType.EAGER)
    private Set<TownPlot> plots = new HashSet<>();

    /**
     * This is the graph of all plots within a town formatted as an adjacency list
     */
    @Transient
    private Map<TownPlot, Set<TownPlot>> plotGraphAdjList;

    private int maxSize;

    private boolean freelyJoinable;

    private boolean pvpEnabled;

    private UUID bank;

    private LocalDateTime lastTaxDate;

    private LocalDateTime lastRaidCreationDate;

    private int taxFailedCount;

    private double debt;

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

    public Set<TownPlot> getPlots() {
        return plots;
    }

    public void setPlots(Set<TownPlot> plots) {
        this.plots = plots;
    }

    public void addPlot(TownPlot plot) {
        plots.add(plot);
    }

    public void removePlot(TownPlot plot) {
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

    public Map<TownPlot, Set<TownPlot>> getPlotGraphAdjList() {
        return plotGraphAdjList;
    }

    public void setPlotGraphAdjList(Map<TownPlot, Set<TownPlot>> plotGraphAdjList) {
        this.plotGraphAdjList = plotGraphAdjList;
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

    public Optional<LocalDateTime> getLastRaidCreationDate() {
        return Optional.ofNullable(this.lastRaidCreationDate);
    }

    public void setLastRaidCreationDate(LocalDateTime lastRaidCreationDate) {
        this.lastRaidCreationDate = lastRaidCreationDate;
    }

    public LocalDateTime getLastTaxDate() {
        return this.lastTaxDate;
    }

    public void setLastTaxDate(LocalDateTime lastTaxDate) {
        this.lastTaxDate = lastTaxDate;
    }

    public int getTaxFailedCount() {
        return this.taxFailedCount;
    }

    public void setTaxFailedCount(int taxFailedCount) {
        this.taxFailedCount = taxFailedCount;
    }

    public double getDebt() {
        return this.debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }
}
