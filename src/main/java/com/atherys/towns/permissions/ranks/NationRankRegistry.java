package com.atherys.towns.permissions.ranks;

import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NationRankRegistry implements CatalogRegistryModule<NationRank> {

    private static NationRankRegistry instance = new NationRankRegistry();

    private Map<String, NationRank> ranks = new HashMap<>();

    private NationRankRegistry () {
    }

    public static NationRankRegistry getInstance () {
        return instance;
    }

    void add ( NationRank rank ) {
        ranks.put( rank.getId(), rank );
    }

    @Override
    public Optional<NationRank> getById ( String id ) {
        return Optional.ofNullable( ranks.get( id ) );
    }

    @Override
    public Collection<NationRank> getAll () {
        return ranks.values();
    }
}
