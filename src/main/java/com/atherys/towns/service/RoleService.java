package com.atherys.towns.service;

import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.model.entity.Town;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Set;

@Singleton
public class RoleService {

    @Inject
    TownsPermissionService townsPermissionService;

    @Inject
    ResidentService residentService;

    PermissionService permissionService;

    private static final String TOWN_PREFIX = "atherystowns.role.town.";

    public String createTownRole(String id, Set<Permission> permissions) {
        String subjectId = TOWN_PREFIX + id;
        SubjectReference ref = permissionService.getGroupSubjects().newSubjectReference(subjectId);

        ref.resolve().thenAccept(subject -> {
            SubjectData subjectData = subject.getTransientSubjectData();
            for (Permission permission : permissions) {
                subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission.getId(), Tristate.TRUE);
            }
        });
        return subjectId;
    }

    public String createNationRole(String id) {
       return id;
    }

    public void setNationRole(User user, String role) {
    }

    public void setTownRole(User user, String role) {
        Subject roleSubject = permissionService.getGroupSubjects().getSubject(role).get();
        Town town = residentService.getOrCreate(user).getTown();

        Set<Context> townContexts = townsPermissionService.getContextsForTown(town);

        user.getSubjectData().clearParents(townContexts).thenAccept(__ -> {
            user.getSubjectData().addParent(townContexts, roleSubject.asSubjectReference());
        });
    }

    private PermissionService getSpongePermissionService() {
        if (permissionService == null) {
            permissionService = Sponge.getServiceManager().provide(org.spongepowered.api.service.permission.PermissionService.class).get();
        }

        return permissionService;
    }

}
