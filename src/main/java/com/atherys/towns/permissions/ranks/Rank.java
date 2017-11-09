package com.atherys.towns.permissions.ranks;

import com.atherys.towns.permissions.actions.TownsAction;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Tristate;

import java.util.LinkedHashSet;
import java.util.List;

public abstract class Rank {

    private String id;
    private String name;
    private List<? extends TownsAction> permittedActions;

    protected Rank ( String id, String name, List<? extends TownsAction> permittedActions ) {
        this.id = id;
        this.name = name;
        this.permittedActions = permittedActions;
    }

    public void addPermissions ( User player ) {
        for ( TownsAction action : permittedActions ) {
            player.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.TRUE );
        }
    }

    public void removePermissions ( User player ) {
        for ( TownsAction action : permittedActions ) {
            player.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.FALSE );
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
