package com.atherys.towns.persistence;

import com.atherys.core.db.AtherysRepository;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.PermissionNode;
import org.slf4j.Logger;

public class PermissionRepository extends AtherysRepository<PermissionNode, Long> {
    protected PermissionRepository() {
        super(PermissionNode.class, AtherysTowns.getLogger());
    }
}
