package com.atherys.towns.plot.flags;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ExtentRegistry implements CatalogRegistryModule<Extent> {

    private static final ExtentRegistry instance = new ExtentRegistry();

    protected Map<String, Extent> extents = new HashMap<>();

    private ExtentRegistry() {
    }

    @Override
    public Optional<Extent> getById ( @Nonnull String id ) {
        return Optional.ofNullable( extents.get( id ) );
    }

    @Override
    public Collection<Extent> getAll () {
        return extents.values();
    }

    public static ExtentRegistry getInstance () {
        return instance;
    }
}
