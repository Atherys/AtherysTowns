package com.atherys.towns.persistence;

import com.atherys.core.db.CachedHibernateRepository;
import com.atherys.towns.api.permission.Permission;
import com.google.inject.Singleton;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Void> deleteAllWithActorId(String actorId) {
        return deleteAllAsync(cache.findAll(node -> node.getActorId().equals(actorId)));
    }

    public CompletableFuture<Void> deleteAllWithActorIdAndSubjectId(String actorId, String subjectId) {
        return deleteAllAsync(cache.findAll(node -> node.getActorId().equals(actorId) && node.getSubjectId().equals(subjectId)));
    }
}
