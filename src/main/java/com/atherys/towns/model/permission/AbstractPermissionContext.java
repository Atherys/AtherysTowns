package com.atherys.towns.model.permission;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.api.permission.ContextHolder;
import com.atherys.towns.api.permission.Contextual;
import com.atherys.towns.api.permission.PermissionContext;
import com.atherys.towns.api.permission.Permissions;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

    private Map<Contextual, Permissions> permissions = new HashMap<>();

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public <C extends Contextual> boolean isPermitted(C contextual, int permissions) {
        Permissions perms = this.permissions.get(contextual);

        if ( perms == null ) {
            return false;
        } else {
            return perms.check(permissions);
        }
    }

    @Override
    public <C extends Contextual> void setPermissions(C contextual, int pemissions) {
        this.permissions.put(contextual, Permissions.of(pemissions));
    }

    @Override
    public <C extends Contextual> void removePermissions(C contextual) {
        this.permissions.remove(contextual);
    }
}
