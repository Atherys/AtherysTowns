package com.atherys.towns.permissions.ranks;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.permissions.actions.TownsAction;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.LinkedHashSet;
import java.util.List;

public abstract class Rank {

    private String id;
    private String name;

    protected Rank child;
    protected Subject permissions;

    protected Rank ( String id, String name, List<? extends TownsAction> permittedActions, Rank child ) {
        this.id = id;
        this.name = name;

        PermissionService service = AtherysTowns.getPermissionService();

        permissions = service.getGroupSubjects().getSubject( id ).get();

        for ( TownsAction action : permittedActions ) {
            permissions.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.TRUE );
        }

        permissions.getSubjectData().addParent( new LinkedHashSet<>(), service.getGroupSubjects().getSubject("atherystowns").get().asSubjectReference() );

        this.child = child;
    }

    public void addPermissions ( User player ) {
        player.getSubjectData().addParent( new LinkedHashSet<>(), permissions.asSubjectReference() );
    }

    public void removePermissions ( User player ) {
        player.getSubjectData().removeParent( new LinkedHashSet<>(), permissions.asSubjectReference() );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Rank getChild() {
        return child;
    }

    public boolean isRankGreaterThan ( Rank rank ) {
        return this.getChild() != null && ( this.getChild() == rank || this.getChild().isRankGreaterThan(rank) );
    }
}
