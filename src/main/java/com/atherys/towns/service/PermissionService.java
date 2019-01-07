package com.atherys.towns.service;

import com.atherys.towns.api.Context;
import com.atherys.towns.api.Contextual;
import com.atherys.towns.api.Permission;
import com.atherys.towns.model.PermissionNode;
import com.atherys.towns.persistence.PermissionRepository;

public class PermissionService {

    private PermissionRepository repository;

    public void permit(Contextual user, Context context, Permission permission) {
        permit(user, context, permission, true);
    }

    public void revoke(Contextual user, Context context, Permission permission) {
        permit(user, context, permission, false);
    }

    public void permit(Contextual user, Context context, Permission permission, boolean permitted) {
        PermissionNode node = new PermissionNode();
        node.setUserId(formatUserId(user));
        node.setContextId(formatContextId(context));
        node.setPermission(permission);
        node.setPermitted(permitted);

        repository.saveOne(node);
        // ...
    }

    public boolean isPermitted(Contextual user, Context context, Permission permission) {

        String userId = formatUserId(user);
        String contextId = formatContextId(context);

        // check for an explicit permission
        boolean explicit = repository.cacheParallelStream().anyMatch(node ->
                node.getUserId().equals(userId) &&
                node.getContextId().equals(contextId) &&
                node.getPermission().equals(permission) &&
                node.isPermitted()
        );

        // if explicitly permitted, return
        if ( explicit ) return explicit;

        // check for transient permissions
        boolean transientPermitted = context.hasParent() && isPermitted(user, context.getParent(), permission);

        // if transiently permitted, return
        if ( transientPermitted ) return transientPermitted;

        // if the user being checked is also a context, check it's parents for explicit and transient permissions
        if ( user instanceof Context ) {

            if ( !((Context) user).hasParent() ) return false;

            Context parent = ((Context) user).getParent();

            return (parent instanceof Contextual) && isPermitted((Contextual) parent, context, permission);

        }

        return false;

    }

    private String formatUserId(Contextual user) {
        return String.format("%s{%s}", user.getClass().getSimpleName(), user.getUniqueId().toString());
    }

    private String formatContextId(Context context) {
        return String.format("%s{%s}", context.getClass().getSimpleName(), context.getUniqueId().toString());
    }

}
