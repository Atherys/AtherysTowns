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
        // TODO: Optimize this

        getCache().values().forEach(node -> {
            if (node.getActorId().equals(actorId)) {
                getCache().remove(node.getId());
            }
        });

        execute("DELETE FROM PermissionNode n WHERE n.actor_id=:actorId",
                query -> query.setParameter("actorId", actorId));
    }

    public void deleteAllWithActorIdAndSubjectId(String actorId, String subjectId) {
        // TODO: Optimize this

        getCache().values().forEach(node -> {
            if (node.getActorId().equals(actorId) && node.getSubjectId().equals(subjectId)) {
                getCache().remove(node.getId());
            }
        });

        execute("DELETE FROM PermissionNode n WHERE n.actor_id=:actorId AND n.subject_id=:subjectId",
                query -> {
                    query.setParameter("actorId", actorId);
                    query.setParameter("subjectId", subjectId);
                });
    }
}
