package com.atherys.towns.plot.flags;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class FlagRegistry implements CatalogRegistryModule<Flag> {

    private static final FlagRegistry instance = new FlagRegistry();

    protected Map<String, Flag> flags = new HashMap<>();

    private FlagRegistry() {
    }

    @Override
    public Optional<Flag> getById ( @Nonnull String id ) {
        return Optional.ofNullable( flags.get( id ) );
    }

    @Override
    public Collection<Flag> getAll () {
        return flags.values();
    }

    public static FlagRegistry getInstance () {
        return instance;
    }
}

