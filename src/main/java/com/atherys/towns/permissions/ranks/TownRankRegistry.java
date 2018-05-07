package com.atherys.towns.permissions.ranks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.api.registry.CatalogRegistryModule;

public final class TownRankRegistry implements CatalogRegistryModule<TownRank> {

  private static TownRankRegistry instance = new TownRankRegistry();

  private Map<String, TownRank> ranks = new HashMap<>();

  private TownRankRegistry() {
  }

  public static TownRankRegistry getInstance() {
    return instance;
  }

  void add(TownRank rank) {
    ranks.put(rank.getId(), rank);
  }

  @Override
  public Optional<TownRank> getById(String id) {
    return Optional.ofNullable(ranks.get(id));
  }

  @Override
  public Collection<TownRank> getAll() {
    return ranks.values();
  }

}
