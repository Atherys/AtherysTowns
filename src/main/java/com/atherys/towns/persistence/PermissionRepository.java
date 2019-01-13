package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.model.PermissionNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

@Singleton
public class PermissionRepository extends AtherysRepository<PermissionNode, Long> {
    @Inject
    protected PermissionRepository(Logger logger) {
        super(PermissionNode.class, logger);
    }
}
