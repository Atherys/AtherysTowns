package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Nation implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String name;

    @Convert(converter = TextConverter.class)
    private Text description;

    @OneToOne
    private Resident leader;

    @OneToOne
    private Town capital;

    @OneToMany(mappedBy = "nation", fetch = FetchType.EAGER)
    private Set<Town> towns = new HashSet<>();

    private boolean joinable;

    private double tax;

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

    public Set<Town> getTowns() {
        return towns;
    }

    public void setTowns(Set<Town> towns) {
        this.towns = towns;
    }

    public void addTown(Town town) {
        towns.add(town);
    }

    public void removeTown(Town town) {
        towns.remove(town);
    }

    public Resident getLeader() {
        return leader;
    }

    public void setLeader(Resident resident) {
        this.leader = resident;
    }

    public Town getCapital() {
        return capital;
    }

    public void setCapital(Town town) {
        this.capital = town;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean freelyJoinable) {
        this.joinable = freelyJoinable;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public UUID getBank() {
        return bank;
    }

    public void setBank(UUID bank) {
        this.bank = bank;
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

}
