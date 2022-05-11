package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.core.db.SpongeIdentifiable;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(schema = "atherystowns", name = "Resident")
public class Resident implements SpongeIdentifiable, Identifiable<UUID> {

    @Id
    private UUID id;

    private String name;

    private String title;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "town_id")
    private Town town;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            schema = "atherystowns",
            name = "Resident_friends",
            joinColumns = @JoinColumn(name = "resident_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Resident> friends = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "atherystowns", name = "Resident_townRoleIds")
    private Set<String> townRoleIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "atherystowns", name = "Resident_nationRoleIds")
    private Set<String> nationRoleIds = new HashSet<>();

    private LocalDateTime registeredOn;

    private LocalDateTime lastLogin;

    private LocalDateTime lastTownSpawn;

    private boolean isFake;

    @OneToMany(mappedBy = "renter", fetch = FetchType.EAGER)
    private Set<RentInfo> tenantPlots = new HashSet<>();

    @Transient
    private int warmupSecondsLeft;

    @Version
    private int version;

    @Nonnull
    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Set<Resident> getFriends() {
        return friends;
    }

    public void setFriends(Set<Resident> friends) {
        this.friends = friends;
    }

    public void addFriend(Resident friend) {
        this.friends.add(friend);
    }

    public void removeFriend(Resident friend) {
        this.friends.remove(friend);
    }

    public Set<String> getTownRoleIds() {
        return townRoleIds;
    }

    public void setTownRoles(Set<String> townRoles) {
        this.townRoleIds = townRoles;
    }

    public Set<String> getNationRoleIds() {
        return nationRoleIds;
    }

    public void setNationRoles(Set<String> nationRoles) {
        this.nationRoleIds = nationRoles;
    }

    public LocalDateTime getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDateTime registeredOn) {
        this.registeredOn = registeredOn;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastTownSpawn() {
        return lastTownSpawn;
    }

    public void setLastTownSpawn(LocalDateTime lastTownSpawn) {
        this.lastTownSpawn = lastTownSpawn;
    }

    public int getWarmupSecondsLeft() {
        return warmupSecondsLeft;
    }

    public void setWarmupSecondsLeft(int warmupSecondsLeft) {
        this.warmupSecondsLeft = warmupSecondsLeft;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setFake(boolean fake) {
        isFake = fake;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Set<RentInfo> getTenantPlots() {
        return tenantPlots;
    }

    public void setTenantPlots(Set<RentInfo> tenantPlots) {
        this.tenantPlots = tenantPlots;
    }

    public void addTenantPlot(RentInfo rentInfo) {
        tenantPlots.add(rentInfo);
    }

    public void removeTenantPlot(RentInfo rentInfo) {
        tenantPlots.remove(rentInfo);
    }
}
