package com.atherys.towns.model.entity;

import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.persistence.converter.PermissionConverter;
import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.text.Text;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TownPlot extends Plot {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "town_id")
    private Town town;

    @Convert(converter = TextConverter.class)
    private Text name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Resident owner;

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> friendPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> townPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> allyPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> enemyPermissions = new HashSet<>();

    @Convert(converter = PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<WorldPermission> neutralPermissions = new HashSet<>();

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Resident getOwner() {
        return owner;
    }

    public void setOwner(Resident owner) {
        this.owner = owner;
    }

    public Set<WorldPermission> getFriendPermissions() {
        return this.friendPermissions;
    }

    public void setFriendPermissions(Set<WorldPermission> friendPermissions) {
        this.friendPermissions = friendPermissions;
    }

    public Set<WorldPermission> getTownPermissions() {
        return this.townPermissions;
    }

    public void setTownPermissions(Set<WorldPermission> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public Set<WorldPermission> getAllyPermissions() {
        return this.allyPermissions;
    }

    public void setAllyPermissions(Set<WorldPermission> allyPermissions) {
        this.allyPermissions = allyPermissions;
    }

    public Set<WorldPermission> getEnemyPermissions() {
        return this.enemyPermissions;
    }

    public void setEnemyPermissions(Set<WorldPermission> enemyPermissions) {
        this.enemyPermissions = enemyPermissions;
    }

    public Set<WorldPermission> getNeutralPermissions() {
        return this.neutralPermissions;
    }

    public void setNeutralPermissions(Set<WorldPermission> neutralPermissions) {
        this.neutralPermissions = neutralPermissions;
    }
}
