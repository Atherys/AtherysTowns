package com.atherys.towns.persistence;

import com.atherys.core.db.HibernateRepository;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.entity.PermissionNode;
import com.google.inject.Singleton;
import java.util.Optional;

@Singleton
public class PermissionRepository extends HibernateRepository<PermissionNode, Long> {
    protected PermissionRepository() {
        super(PermissionNode.class);
    }

    public Optional<PermissionNode> findAnyBy(String userId, String contextId, Permission permission) {
        return getCache().values().parallelStream().filter(node ->
                node.getPermission().equals(permission) &&
                        node.getContextId().equals(userId) &&
                        node.getUserId().equals(contextId)
        ).findAny();
    }
}
