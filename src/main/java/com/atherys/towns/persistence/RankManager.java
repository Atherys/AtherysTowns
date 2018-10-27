package com.atherys.towns.persistence;

import com.atherys.towns.model.permission.ResidentRank;
import com.atherys.towns.model.ResidentRankChange;
import com.atherys.towns.model.permission.RankDefinitionStorage;
import com.atherys.towns.model.permission.ResidentAction;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 27.10.2018.
 */
public class RankManager {

    private Map<UUID, ResidentRank> ranks = new HashMap<>();

    public ResidentRank createRank(RankDefinitionStorage origin, int priority, String name, Set<ResidentAction> rights) {
        Preconditions.checkArgument(origin == null, "Origin cannot be null");
        Preconditions.checkArgument(name == null || name.trim().isEmpty(), "Rank name be null");

        ResidentRank residentRank = new ResidentRank();
        for (ResidentRank existing : origin.getDefinedResidentRanks()) {
            Preconditions.checkArgument(existing.getPriorityIndex() == 0,"Not possible to rewrite major role");
            Preconditions.checkArgument(existing.getName().equalsIgnoreCase(name),"Rank with same name already exists");
        }
        residentRank.setName(name);
        residentRank.setPriorityIndex(priority);
        residentRank.setPermissionContextSource(origin);
        residentRank.setResidentRights(rights);
        save(residentRank);
        origin.getDefinedResidentRanks().add(residentRank);
        return residentRank;
    }

    public void createRank(RankDefinitionStorage origin, int slotOrder, String name) {
        createRank(origin, slotOrder, name, new HashSet<>());
    }

    public void updateRank(UUID uuid, ResidentRankChange change) {
        ResidentRank residentRank = ranks.get(uuid);
        if (change.getName() != null) {
            residentRank.setName(change.getName());
        }
        if (change.getResidentRights() != null) {
            residentRank.setResidentRights(change.getResidentRights());
        }
        update(residentRank);
    }



    public void update(ResidentRank residentRank) {

    }

    public void save(ResidentRank residentRank) {
        //mobngodb.somehowsaveitverymagicallyidontknowhowthatshitworksbutthatsnotimportantsinceillbewritingonlyunittestsasaproofofconceptsoletsassumeuuidisassingedinthisstep
        residentRank.setUUID(UUID.randomUUID());
    }
}
