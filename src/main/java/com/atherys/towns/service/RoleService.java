package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.config.TownRoleConfig;
import com.atherys.towns.model.Nation;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
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
    private static final String NATION_PREFIX = "atherystowns.role.nation.";

    public void init() {
        config.NATIONS.forEach(nationConfig -> {
            for (NationRoleConfig roleConfig : nationConfig.getRoles()) {
                String roleId = nationConfig.getId() + "-" + roleConfig.getId();

                getSpongePermissionService().getGroupSubjects().hasSubject(roleId).thenAcceptAsync(roleExists -> {
                    createRole(NATION_PREFIX + roleId, roleConfig.getNationPermissions(), Collections.emptySet());
                });
            }
        });

        for (TownRoleConfig roleConfig : config.TOWN.ROLES) {
            createRole(TOWN_PREFIX + roleConfig.getId(), roleConfig.getTownPermissions(), roleConfig.getWorldPermissions());
        }
    }

    public void createRole(String id, Set<? extends Permission> permissions, Set<WorldPermission> worldPermissions) {
        SubjectReference ref = permissionService.getGroupSubjects().newSubjectReference(id);

        ref.resolve().thenAccept(subject -> {
            SubjectData subjectData = subject.getTransientSubjectData();

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
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(NATION_PREFIX + nation.getId() + "-" + role).get();

        Set<Context> nationContext = townsPermissionService.getContextForNation(nation);

        subject.getTransientSubjectData().addParent(nationContext, roleSubject.asSubjectReference());
    }

    public void removeNationRole(Subject subject, Nation nation, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(NATION_PREFIX + nation.getId() + "-" + role).get();

        Set<Context> nationContext = townsPermissionService.getContextForNation(nation);

        subject.getTransientSubjectData().removeParent(nationContext, roleSubject.asSubjectReference());
    }

    public void addTownRole(User user, Town town, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(TOWN_PREFIX + role).get();

        Set<Context> townContexts = townsPermissionService.getContextsForTown(town);

        user.getTransientSubjectData().addParent(townContexts, roleSubject.asSubjectReference());
    }

    public void removeTownRole(User user, Town town, String role) {
        Subject roleSubject = getSpongePermissionService().getGroupSubjects().getSubject(TOWN_PREFIX + role).get();

        Set<Context> townContexts = townsPermissionService.getContextsForTown(town);

        user.getTransientSubjectData().removeParent(townContexts, roleSubject.asSubjectReference());
    }

    private PermissionService getSpongePermissionService() {
        if (permissionService == null) {
            permissionService = Sponge.getServiceManager().provide(PermissionService.class).get();
        }

        return permissionService;
    }
}
