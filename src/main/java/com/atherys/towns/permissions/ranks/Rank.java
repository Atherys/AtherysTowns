package com.atherys.towns.permissions.ranks;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.permissions.actions.TownsAction;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public abstract class Rank {

    private String id;
    private String name;
    protected Subject permissions;

    protected Rank ( String id, String name, List<? extends TownsAction> permittedActions ) {
        this.id = id;
        this.name = name;

        Optional<PermissionService> service = AtherysTowns.getInstance().getPermissionService();
        if ( !service.isPresent() ) {
            AtherysTowns.getInstance().getLogger().error("Permission service not found.");
            return;
        }

        permissions = service.get().getGroupSubjects().get( id );

        for ( TownsAction action : permittedActions ) {
            permissions.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.TRUE );
        }

        permissions.getSubjectData().addParent( new LinkedHashSet<>(), service.get().getGroupSubjects().get("atherystowns") );
    }

    public void addPermissions ( User player ) {
        player.getSubjectData().addParent( new LinkedHashSet<>(), permissions );
        //for ( TownsAction action : permittedActions ) {
        //    player.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.TRUE );
        //}
    }

    public void removePermissions ( User player ) {
        player.getSubjectData().removeParent( new LinkedHashSet<>(), permissions );
        //for ( TownsAction action : permittedActions ) {
        //    player.getSubjectData().setPermission( new LinkedHashSet<>(), action.getPermission(), Tristate.FALSE );
        //}
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
