package com.atherys.towns.service;

import com.atherys.towns.api.permission.Permission;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.Subject;

import java.util.List;
import java.util.Set;

@Singleton
public class RoleService {

    @Inject
    PermissionService permissionService;

    org.spongepowered.api.service.permission.PermissionService spongePermissionService;

    public String createTownRole(String id, Set<Permission> permissions) {
        return id;
    }

    public String createNationRole(String id) {
       return id;
    }

    public void setNationRole(User user, String role) {
    }

    public void setTownRole(User user, String role) {

    }

    private org.spongepowered.api.service.permission.PermissionService getSpongePermissionService() {
        if (spongePermissionService == null) {
            spongePermissionService = Sponge.getServiceManager().provide(org.spongepowered.api.service.permission.PermissionService.class).get();
        }

        return spongePermissionService;
    }

}
