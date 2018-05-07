package com.atherys.towns.permissions.actions;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(NationActions.class)
public class NationAction implements CatalogType, TownsAction {

  private String id;
  private String name;
  private String permission;

  public NationAction(String id, String name, String permission) {
    this.id = id;
    this.name = name;
    this.permission = permission;

    NationActionRegistry.getInstance().add(this);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getPermission() {
    return permission;
  }
}
