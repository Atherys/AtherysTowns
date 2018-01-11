package com.atherys.towns.permissions.actions;

import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TownActionRegistry implements CatalogRegistryModule<TownAction> {

    private static TownActionRegistry instance = new TownActionRegistry();

    private Map<String,TownAction> actions = new HashMap<>();

    private TownActionRegistry() {}

    public static TownActionRegistry getInstance() {
        return instance;
    }

    void add ( TownAction rank ) {
        actions.put( rank.getId(), rank );
    }

    @Override
    public Optional<TownAction> getById(String id) {
        return Optional.ofNullable( actions.get(id) );
    }

    @Override
    public Collection<TownAction> getAll() {
        return actions.values();
    }
}
