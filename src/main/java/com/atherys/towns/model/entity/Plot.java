package com.atherys.towns.model.entity;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.persistence.converter.PermissionConverter;
import com.atherys.towns.persistence.converter.TextConverter;
import com.atherys.towns.persistence.converter.Vector2iConverter;
import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Plot implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "town_id")
    private Town town;

    @Convert(converter = TextConverter.class)
    private Text name;

    @Convert(converter = Vector2iConverter.class)
    private Vector2i swCorner;

    @Convert(converter = Vector2iConverter.class)
    private Vector2i neCorner;

    @ManyToOne(fetch = FetchType.EAGER)
    private Resident owner;


    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> friendPermissions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> townPermissions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> allyPermissions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> enemyPermissions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> neutralPermissions = new HashSet<>();

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

    public Vector2i getSouthWestCorner() {
        return swCorner;
    }

    public void setSouthWestCorner(Vector2i swCorner) {
        this.swCorner = swCorner;
    }

    public Vector2i getNorthEastCorner() {
        return neCorner;
    }

    public void setNorthEastCorner(Vector2i neCorner) {
        this.neCorner = neCorner;
    }

    public Resident getOwner() {
        return owner;
    }

    public void setOwner(Resident owner) {
        this.owner = owner;
    }

    public Set<String> getFriendPermissions() {
        return this.friendPermissions;
    }

    public void setFriendPermissions(Set<String> friendPermissions) {
        this.friendPermissions = friendPermissions;
    }

    public Set<String> getTownPermissions() {
        return this.townPermissions;
    }

    public void setTownPermissions(Set<String> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public Set<String> getAllyPermissions() {
        return this.allyPermissions;
    }

    public void setAllyPermissions(Set<String> allyPermissions) {
        this.allyPermissions = allyPermissions;
    }

    public Set<String> getEnemyPermissions() {
        return this.enemyPermissions;
    }

    public void setEnemyPermissions(Set<String> enemyPermissions) {
        this.enemyPermissions = enemyPermissions;
    }

    public Set<String> getNeutralPermissions() {
        return this.neutralPermissions;
    }

    public void setNeutralPermissions(Set<String> neutralPermissions) {
        this.neutralPermissions = neutralPermissions;
    }

    protected int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }
}
