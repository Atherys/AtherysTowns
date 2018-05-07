package com.atherys.towns.permissions.actions;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(TownActions.class)
public class TownAction implements CatalogType, TownsAction {

  private String id;
  private String name;
  private String permission;

  public TownAction(String id, String name, String permission) {
    this.id = id;
    this.name = name;
    this.permission = permission;
    TownActionRegistry.getInstance().add(this);
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
