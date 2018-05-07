package com.atherys.towns.permissions.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.api.registry.CatalogRegistryModule;

public final class NationActionRegistry implements CatalogRegistryModule<NationAction> {

  private static NationActionRegistry instance = new NationActionRegistry();

  private Map<String, NationAction> actions = new HashMap<>();

  private NationActionRegistry() {
  }

  public static NationActionRegistry getInstance() {
    return instance;
  }

  void add(NationAction rank) {
    actions.put(rank.getId(), rank);
  }

  @Override
  public Optional<NationAction> getById(String id) {
    return Optional.ofNullable(actions.get(id));
  }

  @Override
  public Collection<NationAction> getAll() {
    return actions.values();
  }
}
