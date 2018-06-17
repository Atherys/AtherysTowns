package com.atherys.towns2.rank.action;

import javax.annotation.Nonnull;
import me.lucko.luckperms.api.Node;
import org.spongepowered.api.CatalogType;

public abstract class Action implements CatalogType {

    private String id;
    private String name;

    private Node permission;

    Action(String id, String name, Node permission) {
        this.id = id;
        this.name = name;
    }

    @Override
    @Nonnull
    public String getId() {
        return id;
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    public Node getPermission() {
        return permission;
    }

}
