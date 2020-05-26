package com.atherys.towns.facade;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.config.TownRoleConfig;
import com.atherys.towns.service.TownsPermissionService;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class PermissionFacade {

    @Inject
    TownsConfig config;

    @Inject
    ResidentService residentService;

    @Inject
    TownsPermissionService townsPermissionService;

    @Inject
    PlotService plotService;

    org.spongepowered.api.service.permission.PermissionService spongePermissionService;

    static final String NOT_PERMITTED = "You are not permitted to ";

    public final Map<String, TownPermission> TOWN_PERMISSIONS = getTownPermissions();

    public final Map<String, NationPermission> NATION_PERMISSIONS = getNationPermissions();

    PermissionFacade() {
    }

    public void init() {
        spongePermissionService = Sponge.getServiceManager().provide(org.spongepowered.api.service.permission.PermissionService.class).get();

        config.NATIONS.forEach(nationConfig -> {
            SubjectReference lastRoleProcessed = null;
            for (NationRoleConfig roleConfig : nationConfig.getRoles()) {
                String roleId = nationConfig.getId() + "-" + roleConfig.getId();

                spongePermissionService.getGroupSubjects().hasSubject(roleId).thenAcceptAsync(roleExists -> {
                    if (!roleExists) {
                        createRole(roleId, lastRoleProcessed);
                    }
                });
            }
        });

        SubjectReference lastRoleProcessed = null;
        for (TownRoleConfig roleConfig : config.TOWN.ROLES) {
            String roleId = "town-" + roleConfig.getId();

            if (roleExists(roleId)) {
                continue;
            }

            lastRoleProcessed = createRole(roleId, lastRoleProcessed);
        }
    }

    private boolean roleExists(String id) {
        SubjectReference role = spongePermissionService.getGroupSubjects().newSubjectReference(id);
        return true;
    }

    private SubjectReference createRole(String id, SubjectReference parent) {
        SubjectReference role = spongePermissionService.getGroupSubjects().newSubjectReference(id);

        if (parent != null) {
            role.resolve().thenAcceptAsync(subject -> subject.getSubjectData().addParent(new HashSet<>(), parent));
        }

        return role;
    }

    public boolean isPermitted(Player source, Permission permission) throws TownsCommandException {
        if (permission == null) {
            throw new TownsCommandException("Failed to establish command permission. Will not proceed.");
        }

        return townsPermissionService.isPermitted(source, permission);
    }

    public void checkPermitted(Player source, Permission permission, String message) throws TownsCommandException {
        if (!isPermitted(source, permission)) {
            throw new TownsCommandException(NOT_PERMITTED + message);
        }
    }

    private Map<String, TownPermission> getTownPermissions() {
        Collection<TownPermission> perms = Sponge.getGame().getRegistry().getAllOf(Permission.class).stream()
                .filter(permission -> permission instanceof TownPermission)
                .map(permission -> (TownPermission) permission)
                .collect(Collectors.toList());

        Map<String, TownPermission> townPerms = new HashMap<>();
        perms.forEach(permission -> townPerms.put(permission.getId(), permission));

        return townPerms;
    }

    private Map<String, NationPermission> getNationPermissions() {
        Collection<NationPermission> perms = Sponge.getGame().getRegistry().getAllOf(Permission.class).stream()
                .filter(permission -> permission instanceof NationPermission)
                .map(permission -> (NationPermission) permission)
                .collect(Collectors.toList());

        Map<String, NationPermission> nationPerms = new HashMap<>();
        perms.forEach(permission -> nationPerms.put(permission.getId(), permission));

        return nationPerms;
    }
}
