package com.atherys.towns.service;

import com.atherys.towns.api.permission.Actor;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.Subject;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.role.Role;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.entity.*;
import com.atherys.towns.persistence.PermissionRepository;
import com.google.inject.Singleton;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Singleton
public class PermissionService {

    private PermissionRepository permissionRepository;

    @Inject
    PermissionService(
            PermissionRepository permissionRepository
    ) {
        this.permissionRepository = permissionRepository;
    }

    public void permit(Actor actor, Subject subject, Set<Permission> permissions) {
        permissions.forEach(permission -> permit(actor, subject, permission));
    }

    public void revoke(Actor actor, Subject subject, Set<Permission> permissions) {
        permissions.forEach(permission -> remove(actor, subject, permission, true));
    }

    public void permit(Actor user, Subject subject, Permission permission) {
        permit(user, subject, permission, true);
    }

    public void remove(Actor actor, Subject subject, Permission permission, boolean permitted) {
        PermissionNode node = createPermissionNode(actor, subject, permission, permitted);
        permissionRepository.deleteOne(node);
    }

    /**
     * Create a new PermissionNode object and store it in the database
     *
     * @param user
     * @param subject
     * @param permission
     * @param permitted
     */
    public void permit(Actor user, Subject subject, Permission permission, boolean permitted) {
        PermissionNode node = createPermissionNode(user, subject, permission, permitted);
        permissionRepository.saveOne(node);
    }

    public void grant(Resident resident, TownRole role) {
        resident.getTownRoles().add(role);
    }

    public void grant(Resident resident, NationRole role) {
        resident.getNationRoles().add(role);
    }

    public void revoke(Resident resident, Role role) {

    }

    public PermissionNode createPermissionNode(Actor actor, Subject subject, Permission permission, boolean permitted) {
        PermissionNode node = new PermissionNode();
        PermissionNodeId nodeId = new PermissionNodeId(
                formatActorId(actor),
                formatSubjectId(subject),
                permission
        );

        node.setId(nodeId);
        node.setPermitted(permitted);

        return node;
    }

    /**
     * Check if the provided Actor has the permissions to act upon the provided subject.
     * <br><br>
     * This method will first check if the actor is a Resident, in which case it will go through the
     * resident's nation and town roles, and afterwards it will check for explicit permissions ( see {@link #isExplicitlyPermitted(Actor, Subject, Permission)} )
     *
     * @param actor      The actor ( A resident/town/nation )
     * @param subject    The subject ( A plot/town/nation )
     * @param permission the permission ( also known as an "action" ).
     * @return Whether the Actor is permitted to execute the specified action upon the Subject.
     */
    public boolean isPermitted(Actor actor, Subject subject, Permission permission) {

        if (actor instanceof Resident) {
            Resident resident = (Resident) actor;

            if (subject instanceof Town) {

                if (permission instanceof TownPermission) {
                    // Check for town permissions in the resident's townRole(s)
                    if (resident.getTownRoles().stream().anyMatch(role -> role.getPermissions().contains(permission))) {
                        return true;
                    }
                }

                if (permission instanceof WorldPermission) {
                    // check for world permissions in the resident's townRole(s)
                    if (resident.getTownRoles().stream().anyMatch(role -> role.getWorldPermissions().contains(permission))) {
                        return true;
                    }
                }

            }

            if (subject instanceof Nation && permission instanceof NationPermission) {
                // check for nation permissions in the resident's nationRole(s)
                if (resident.getNationRoles().stream().anyMatch(role -> role.getPermissions().contains(permission))) {
                    return true;
                }
            }
        }

        // if no role permissions could be found, look for explicit ones
        return isExplicitlyPermitted(actor, subject, permission);
    }

    /**
     * Check the database for PermissionNode objects which may grant the actor the provided permission, within the
     * context of the subject.
     * <br><br>
     * Firstly, a check is made for what are known as "explicit" permissions. An explicit permission is one where
     * there is a PermissionNode which grants this specific Actor to act upon this specific Subject in the specified
     * way.
     * <br><br>
     * If an explicit PermissionNode is found which does the opposite, to explicitly deny the Actor this permission,
     * then no further checks will be made.
     * <br><br>
     * If no such PermissionNode is found, the next check to follow is to see whether the Actor is transiently
     * permitted to execute this action.
     * <br><br>
     * A transient permission is when the Actor is permitted the action upon the provided Subject's
     * parent object instead.
     * <br><br>
     * For example, if a Resident is permitted to destroy within a Town, then that means that they are allowed
     * to destroy within all Plots belonging to said town. This is transient, because the permission's subject is
     * not the Plot itself, but rather the Plot's parent object - the Town.
     * <br><br>
     * This method is actually recursive, in that it will call itself to check for an explicit permission where the
     * Actor is the same, but the Subject is now the previous Subject's parent.
     * <br><br>
     * If no transient PermissionNode is found either, then the next check will see if the Actor is itself
     * also a Subject. If so, a check will be made if the Actor's parent Subject ( if any ) is permitted to
     * execute the action in question upon the provided Subject. All previous checks mentioned will also be done.
     * <br><br>
     *
     * Unlike {@link #isPermitted(Actor, Subject, Permission)}, this method will not check role permissions.
     *
     * @param actor      The actor ( A resident/town/nation )
     * @param subject    The subject ( A plot/town/nation )
     * @param permission the permission ( also known as an "action" ).
     * @return Whether the Actor is permitted to execute the specified action upon the Subject.
     */
    public boolean isExplicitlyPermitted(Actor actor, Subject subject, Permission permission) {
        String userId = formatActorId(actor);
        String contextId = formatSubjectId(subject);

        // check for an explicit permission
        Optional<PermissionNode> any = permissionRepository.findAnyBy(userId, contextId, permission);

        // if explicitly permitted, return
        if (any.isPresent()) {
            return any.get().isPermitted();
        }

        // check for transient permissions
        boolean transientPermitted = subject.hasParent() && isPermitted(actor, subject.getParent(), permission);

        // if transiently permitted, return
        if (transientPermitted) {
            return true;
        }

        // if the user being checked is also a subject, check it's parents for explicit and transient permissions
        if (actor instanceof Subject) {

            if (!((Subject) actor).hasParent()) {
                return false;
            }

            Subject parent = ((Subject) actor).getParent();

            return (parent instanceof Actor) && isPermitted((Actor) parent, subject, permission);

        }

        return false;
    }

    public void ifPermitted(Actor actor, Subject subject, Permission permission, Runnable action) {
        if (isPermitted(actor, subject, permission)) action.run();
    }

    private String formatActorId(Actor actor) {
        return String.format("%s{%s}", actor.getClass().getSimpleName(), actor.getId().toString());
    }

    private String formatSubjectId(Subject subject) {
        return String.format("%s{%s}", subject.getClass().getSimpleName(), subject.getId().toString());
    }

    public CompletableFuture<Void> removeAll(Actor actor) {
        return permissionRepository.deleteAllWithActorId(formatActorId(actor));
    }

    public CompletableFuture<Void> removeAll(Actor actor, Subject subject) {
        return permissionRepository.deleteAllWithActorIdAndSubjectId(formatActorId(actor), formatSubjectId(subject));
    }
}
