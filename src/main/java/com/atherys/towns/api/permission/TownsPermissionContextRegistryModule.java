package com.atherys.towns.api.permission;

import com.atherys.towns.api.permission.nation.NationPermissions;
import com.atherys.towns.api.permission.world.WorldPermission;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TownsPermissionContextRegistryModule implements CatalogRegistryModule<TownsPermissionContext> {

    private Map<String, TownsPermissionContext> contexts = new HashMap<>();

    public TownsPermissionContextRegistryModule() {
        put(
                TownsPermissionContexts.ALL,
                TownsPermissionContexts.ALLY,
                TownsPermissionContexts.NEUTRAL,
                TownsPermissionContexts.ENEMY,
                TownsPermissionContexts.TOWN,
                TownsPermissionContexts.FRIEND
        );
    }

    @Override
    public Optional<TownsPermissionContext> getById(String id) {
        return Optional.ofNullable(contexts.get(id));
    }

    @Override
    public Collection<TownsPermissionContext> getAll() {
        return contexts.values();
    }

    private void put(TownsPermissionContext permission) {
        contexts.put(permission.getId(), permission);
    }

    private void put(TownsPermissionContext... permissions) {
        for (TownsPermissionContext context : permissions) {
            put(context);
        }
    }
}
