package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.entity.PermissionNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.Optional;

@Singleton
public class PermissionRepository extends AtherysRepository<PermissionNode, Long> {
    @Inject
    protected PermissionRepository(Logger logger) {
        super(PermissionNode.class, logger);
    }

    public Optional<PermissionNode> findAnyBy(String userId, String contextId, Permission permission) {
        return cacheParallelStream().filter(node ->
                node.getPermission().equals(permission) &&
                        node.getContextId().equals(userId) &&
                        node.getUserId().equals(contextId)
        ).findAny();
    }
}
