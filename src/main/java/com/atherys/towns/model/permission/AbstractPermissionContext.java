package com.atherys.towns.model.permission;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.api.permission.ContextHolder;
import com.atherys.towns.api.permission.Contextual;
import com.atherys.towns.api.permission.PermissionContext;
import com.atherys.towns.api.permission.Permissions;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.atherys.towns.persistence.converter.PermissionsConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nonnull;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKeyJoinColumn;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractPermissionContext<H extends ContextHolder> implements PermissionContext<H>, SpongeIdentifiable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    @ElementCollection
    @CollectionTable(name = "resident_permissions")
    @MapKeyJoinColumn(name = "resident_id")
    @Convert(converter = PermissionsConverter.class, attributeName = "value")
    private Map<Resident, Permissions> residentPermissions = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "town_permissions")
    @MapKeyJoinColumn(name = "town_id")
    @Convert(converter = PermissionsConverter.class, attributeName = "value")
    private Map<Town, Permissions> townPermissions = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "nation_permissions")
    @MapKeyJoinColumn(name = "nation_id")
    @Convert(converter = PermissionsConverter.class, attributeName = "value")
    private Map<Nation, Permissions> nationPermissions = new HashMap<>();

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    public Map<Nation, Permissions> getNationPermissions() {
        return nationPermissions;
    }

    public Map<Town, Permissions> getTownPermissions() {
        return townPermissions;
    }

    public Map<Resident, Permissions> getResidentPermissions() {
        return residentPermissions;
    }

    public void setNationPermissions(Map<Nation, Permissions> nationPermissions) {
        this.nationPermissions = nationPermissions;
    }

    public void setTownPermissions(Map<Town, Permissions> townPermissions) {
        this.townPermissions = townPermissions;
    }

    public void setResidentPermissions(Map<Resident, Permissions> residentPermissions) {
        this.residentPermissions = residentPermissions;
    }
}
