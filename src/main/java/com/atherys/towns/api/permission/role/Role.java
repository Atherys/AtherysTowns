package com.atherys.towns.api.permission.role;

import com.atherys.core.db.Identifiable;
import com.atherys.towns.api.permission.Permission;

import java.util.Set;

public interface Role<P extends Permission> extends Identifiable<Long> {

    String getShortName();

    String getName();

    Set<P> getPermissions();

}
