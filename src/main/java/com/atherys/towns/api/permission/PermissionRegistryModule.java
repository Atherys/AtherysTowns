package com.atherys.towns.api.permission;

import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.api.permission.town.TownPermissions;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PermissionRegistryModule implements CatalogRegistryModule<Permission> {

    private Map<String, Permission> permissions = new HashMap<>();

    public PermissionRegistryModule() {
        put(
                NationPermissions.INVITE_TOWN,
                NationPermissions.KICK_TOWN,
                NationPermissions.SET_PERMISSION,
                NationPermissions.SET_ROLE,
                NationPermissions.WITHDRAW_FROM_BANK,
                NationPermissions.DEPOSIT_INTO_BANK,
                NationPermissions.CHAT,
                NationPermissions.ADD_ALLY,
                NationPermissions.ADD_ENEMY,
                NationPermissions.ADD_NEUTRAL
        );

        put(
                TownPermissions.INVITE_RESIDENT,
                TownPermissions.KICK_RESIDENT,
                TownPermissions.CLAIM_PLOT,
                TownPermissions.UNCLAIM_PLOT,
                TownPermissions.RENAME_PLOT,
                TownPermissions.GRANT_PLOT,
                TownPermissions.SET_PERMISSION,
                TownPermissions.SET_ROLE,
                TownPermissions.RUIN_TOWN,
                TownPermissions.START_RAID,
                TownPermissions.CANCEL_RAID,
                TownPermissions.WITHDRAW_FROM_BANK,
                TownPermissions.DEPOSIT_INTO_BANK,
                TownPermissions.JOIN_NATION,
                TownPermissions.SET_NAME,
                TownPermissions.SET_DESCRIPTION,
                TownPermissions.SET_MOTD,
                TownPermissions.SET_COLOR,
                TownPermissions.SET_FREELY_JOINABLE,
                TownPermissions.SET_SPAWN,
                TownPermissions.SET_PVP,
                TownPermissions.TRANSFER_LEADERSHIP,
                TownPermissions.CHAT,
                TownPermissions.DEBT_PAY
        );
    }

    private void put(Permission permission) {
        permissions.put(permission.getId(), permission);
    }

    private void put(Permission... permissions) {
        for (Permission permission : permissions) {
            put(permission);
        }
    }

    @Override
    public Optional<Permission> getById(String id) {
        return Optional.ofNullable(permissions.get(id));
    }

    @Override
    public Collection<Permission> getAll() {
        return permissions.values();
    }
}
