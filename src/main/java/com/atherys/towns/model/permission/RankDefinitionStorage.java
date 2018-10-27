package com.atherys.towns.model.permission;

import java.util.Set;

/**
 * Created by NeumimTo on 27.10.2018.
 */
public interface RankDefinitionStorage extends PermissionContext {

    Set<ResidentRank> getDefinedResidentRanks();
}
