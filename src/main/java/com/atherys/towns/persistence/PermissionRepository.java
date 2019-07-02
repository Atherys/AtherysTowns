package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.entity.PermissionNode;
import com.atherys.towns.entity.PermissionNodeId;
import com.google.inject.Singleton;

import java.util.Optional;

@Singleton
public class PermissionRepository extends CachedHibernateRepository<PermissionNode, PermissionNodeId> {

    protected PermissionRepository() {
        super(PermissionNode.class);
    }

    public Optional<PermissionNode> findAnyBy(String actorId, String subjectId, Permission permission) {
        return cache.findOne(node ->
                node.getActorId().equals(actorId) &&
                        node.getSubjectId().equals(subjectId) &&
                        node.getPermission().equals(permission)
        );
    }

    public void deleteAllWithActorId(String actorId) {
        deleteAllAsync(cache.findAll(node -> node.getActorId().equals(actorId)));
    }

    public void deleteAllWithActorIdAndSubjectId(String actorId, String subjectId) {
        deleteAllAsync(cache.findAll(node -> node.getActorId().equals(actorId) && node.getSubjectId().equals(subjectId)));
    }
}
