package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Resident;
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

    private static final String TOWN_PREFIX = "role.town.";
    private static final String NATION_PREFIX = "role.nation.";
    @Inject
    TownsPermissionService townsPermissionService;
    @Inject
    ResidentService residentService;
    @Inject
    TownsConfig config;
    PermissionService permissionService;

    public void init() {
        config.NATION.ROLES.forEach((id, roleConfig) -> {
            createRole(NATION_PREFIX + id, roleConfig.getNationPermissions(), Collections.emptySet());
        });

        config.TOWN.ROLES.forEach((id, roleConfig) -> {
            createRole(TOWN_PREFIX + id, roleConfig.getTownPermissions(), roleConfig.getWorldPermissions());
        });

    }

    public void createRole(String id, Set<? extends Permission> permissions, Set<WorldPermission> worldPermissions) {
        SubjectReference ref = getSpongePermissionService().getGroupSubjects().newSubjectReference(id);

        ref.resolve().thenAccept(subject -> {
            SubjectData subjectData = subject.getSubjectData();
            subjectData.clearPermissions();

            for (Permission permission : permissions) {
                subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission.getId(), Tristate.TRUE);
                AtherysTowns.getInstance().getLogger().info(permission.getId());
            }

            for (WorldPermission permission : worldPermissions) {
                subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission.getId(), Tristate.TRUE);
            }
        });
    }

    public void addNationRole(Subject subject, Nation nation, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(NATION_PREFIX + role).get();

        Set<Context> nationContext = townsPermissionService.getContextForNation(nation);

        subject.getSubjectData().addParent(nationContext, roleSubject.asSubjectReference());
    }

    public void removeNationRole(Subject subject, Nation nation, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(NATION_PREFIX + role).get();

        Set<Context> nationContext = townsPermissionService.getContextForNation(nation);

        subject.getSubjectData().removeParent(nationContext, roleSubject.asSubjectReference());
    }

    public void addTownRole(User user, Town town, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(TOWN_PREFIX + role).get();

        Set<Context> townContexts = townsPermissionService.getContextsForTown(town);

        user.getSubjectData().addParent(townContexts, roleSubject.asSubjectReference());
    }

    public void removeTownRole(User user, Town town, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(TOWN_PREFIX + role).get();

        Set<Context> townContexts = townsPermissionService.getContextsForTown(town);

        user.getSubjectData().removeParent(townContexts, roleSubject.asSubjectReference());
    }

    public void validateRoles(User user, Resident resident) {
        for (String role : resident.getTownRoleIds()) {
            if (!config.TOWN.ROLES.containsKey(role)) {
                residentService.removeTownRole(resident, role);
            }
        }

        for (String role : resident.getNationRoleIds()) {
            if (!config.NATION.ROLES.containsKey(role)) {
                residentService.removeNationRole(resident, role);
            }
        }
    }

    private PermissionService getSpongePermissionService() {
        if (permissionService == null) {
            permissionService = Sponge.getServiceManager().provide(PermissionService.class).get();
        }

        return permissionService;
    }
}
