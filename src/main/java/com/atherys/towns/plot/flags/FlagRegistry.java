package com.atherys.towns.plot.flags;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.registry.CatalogRegistryModule;

public final class FlagRegistry implements CatalogRegistryModule<Flag> {

  private static final FlagRegistry instance = new FlagRegistry();

  protected Map<String, Flag> flags = new HashMap<>();

  private FlagRegistry() {
  }

  public static FlagRegistry getInstance() {
    return instance;
  }

  @Override
  public Optional<Flag> getById(@Nonnull String id) {
    return Optional.ofNullable(flags.get(id));
  }

  @Override
  public Collection<Flag> getAll() {
    return flags.values();
  }
}

