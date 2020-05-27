package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.config.TownRoleConfig;
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

import java.util.Collections;
import java.util.Set;

@Singleton
public class RoleService {

    @Inject
    TownsPermissionService townsPermissionService;

    @Inject
    ResidentService residentService;

    @Inject
    TownsConfig config;

    PermissionService permissionService;

    private static final String TOWN_PREFIX = "atherystowns.role.town.";

    public void init() {
        config.NATIONS.forEach(nationConfig -> {
            for (NationRoleConfig roleConfig : nationConfig.getRoles()) {
                String roleId = nationConfig.getId() + "-" + roleConfig.getId();

                getSpongePermissionService().getGroupSubjects().hasSubject(roleId).thenAcceptAsync(roleExists -> {
                    createRole(roleId, roleConfig.getNationPermissions(), Collections.emptySet());
                });
            }
        });

        for (TownRoleConfig roleConfig : config.TOWN.ROLES) {
            createRole(roleConfig.getId(), roleConfig.getTownPermissions(), roleConfig.getWorldPermissions());
        }
    }

    public void createRole(String id, Set<? extends Permission> permissions, Set<WorldPermission> worldPermissions) {
        SubjectReference ref = permissionService.getGroupSubjects().newSubjectReference(id);

        ref.resolve().thenAccept(subject -> {
            SubjectData subjectData = subject.getTransientSubjectData();

            for (Permission permission : permissions) {
                subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission.getId(), Tristate.TRUE);
            }

            for (WorldPermission permission : worldPermissions) {
                subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission.getId(), Tristate.TRUE);
            }
        });
    }

    public void setNationRole(User user, String role) {
    }

    public void setTownRole(User user, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(role).get();
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
