package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.permission.PermissionPrototype;
import com.atherys.towns.model.permission.ResidentPermission;
import me.lucko.luckperms.LuckPerms;
import org.spongepowered.api.util.Identifiable;

public class PermissionManager extends MorphiaDatabaseManager<ResidentPermission> {

    public PermissionManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), ResidentPermission.class);
    }

    public <T extends Identifiable> ResidentPermission<T> givePermission(Resident resident, T context, PermissionPrototype<T> prototype) {
        ResidentPermission<T> permission = new ResidentPermission<>(resident, context, prototype);

        LuckPerms.getApi().getUserSafe(resident.getUniqueId()).ifPresent(user -> {
            user.setPermission(LuckPerms.getApi().buildNode(prototype.getPermission()).setValue(true).build());
            LuckPerms.getApi().getUserManager().saveUser(user);
        });

        return permission;
    }

    public <T extends Identifiable> void revokePermission(Resident resident, T context, PermissionPrototype<T> prototype) {
        ResidentPermission<T> permission = new ResidentPermission<>(resident, context, prototype);
        super.remove(permission);
    }

}
