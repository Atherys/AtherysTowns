package com.atherys.towns.persistence;

import com.atherys.core.database.mongo.MorphiaDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.permission.ResidentRights;
import com.atherys.towns.model.permission.ResidentRank;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.atherys.towns.model.permission.ResidentAction;
import com.google.common.base.Preconditions;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NationManager extends MorphiaDatabaseManager<Nation> {

    public NationManager() {
        super(AtherysTowns.getDatabase(), AtherysTowns.getLogger(), Nation.class);
    }

    public void createNation(Town origin, String name) {
        Preconditions.checkArgument(!origin.getNation().isPresent(), "Town already in a nation");
        Preconditions.checkArgument(name != null, "Nation name cannot be null");
        Optional<Nation> any = getCache().values().stream().filter(a -> a.getName().toLowerCase().trim().equalsIgnoreCase(name)).findAny();
        any.ifPresent(a->{
            throw new RuntimeException("Nation name not unique");
        });
        Nation nation = new Nation(origin);
        nation.setName(name);



        origin.setNation(nation);

        Resident owner = null;
        for (Resident resident : origin.getResidents()) {
            Set<ResidentRank> residentRanks = resident.getResidentRanks();
            for (ResidentRank residentRank : residentRanks) {
                if (residentRank.getPriorityIndex() == ResidentRank.OWNER) {
                    owner = resident;
                }
            }
        }

        Preconditions.checkArgument(owner != null, "Town has no owner, how did you do that?");

        //for sake of simplicity lets assume this bellow is all magically synced, im not going to write mocks for sponge scheduler
        ResidentRank ownerResidentRank = AtherysTowns.getRankManager().createRank(nation, ResidentRank.OWNER, "King", new HashSet<ResidentAction>() {{
            add(ResidentRights.INVITE_MEMBER);
            add(ResidentRights.KICK_MEMBER);
        }});
        nation.getDefinedResidentRanks().add(ownerResidentRank);
        save(nation);
        AtherysTowns.getResidentManager().addRank(owner, ownerResidentRank);
        AtherysTowns.getTownManager().addTownToNation(origin, nation);
    }
}
