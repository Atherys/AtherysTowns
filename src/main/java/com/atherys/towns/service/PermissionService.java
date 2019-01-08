package com.atherys.towns.service;

import com.atherys.towns.api.Subject;
import com.atherys.towns.api.Actor;
import com.atherys.towns.api.Permission;
import com.atherys.towns.model.PermissionNode;
import com.atherys.towns.persistence.PermissionRepository;

public class PermissionService {

    private PermissionRepository repository;

    public void permit(Actor user, Subject subject, Permission permission) {
        permit(user, subject, permission, true);
    }

    public void revoke(Actor user, Subject subject, Permission permission) {
        permit(user, subject, permission, false);
    }

    public void permit(Actor user, Subject subject, Permission permission, boolean permitted) {
        PermissionNode node = new PermissionNode();
        node.setUserId(formatUserId(user));
        node.setContextId(formatContextId(subject));
        node.setPermission(permission);
        node.setPermitted(permitted);

        repository.saveOne(node);
        // ...
    }

    public boolean isPermitted(Actor user, Subject subject, Permission permission) {

        String userId = formatUserId(user);
        String contextId = formatContextId(subject);

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
        boolean transientPermitted = subject.hasParent() && isPermitted(user, subject.getParent(), permission);

        // if transiently permitted, return
        if ( transientPermitted ) return transientPermitted;

        // if the user being checked is also a subject, check it's parents for explicit and transient permissions
        if ( user instanceof Subject) {

            if ( !((Subject) user).hasParent() ) return false;

            Subject parent = ((Subject) user).getParent();

            return (parent instanceof Actor) && isPermitted((Actor) parent, subject, permission);

        }

        return false;

    }

    private String formatUserId(Actor user) {
        return String.format("%s{%s}", user.getClass().getSimpleName(), user.getUniqueId().toString());
    }

    private String formatContextId(Subject subject) {
        return String.format("%s{%s}", subject.getClass().getSimpleName(), subject.getUniqueId().toString());
    }

}
