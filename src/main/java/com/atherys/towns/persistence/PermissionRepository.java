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
                        node.getSubjectId().equals(userId) &&
                        node.getActorId().equals(contextId)
        ).findAny();
    }

    public void deleteAllWithActorId(String actorId) {
        // TODO: Clear PermissionNodes from cache as well
        execute("DELETE FROM PermissionNode n WHERE n.actor_id=:actorId",
                query -> query.setParameter("actorId", actorId));
    }

    public void deleteAllWithActorIdAndSubjectId(String actorId, String subjectId) {
        // TODO: Clear PermissionNodes from cache as well
        execute("DELETE FROM PermissionNode n WHERE n.actor_id=:actorId AND n.subject_id=:subjectId",
                query -> {
                    query.setParameter("actorId", actorId);
                    query.setParameter("subjectId", subjectId);
                });
    }
}
