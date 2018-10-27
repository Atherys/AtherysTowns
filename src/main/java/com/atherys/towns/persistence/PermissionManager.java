package com.atherys.towns.persistence;

import com.atherys.towns.model.Resident;
import com.atherys.towns.model.permission.ResidentAction;
import com.atherys.towns.model.permission.ResidentRank;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;

public class PermissionManager  {

    public PermissionManager() {
    }

    /**
     * Called after player login
     * "Translates" town and nation ranks to a standart permission format, which sponge understands
     * @param resident
     */
    public void resolveResidentPermissions(Resident resident) {
        Optional<Player> playerOpt = Sponge.getGame().getServer().getPlayer(resident.getUUID());
        playerOpt.ifPresent(player -> {
            Set<ResidentRank> residentRanks = resident.getResidentRanks();
            SubjectData subjectData = player.getTransientSubjectData();
            for (ResidentRank residentRank : residentRanks) {
                Set<ResidentAction> residentRights = residentRank.getResidentRights();
                for (ResidentAction residentRight : residentRights) {
                    subjectData.setPermission(SubjectData.GLOBAL_CONTEXT,
                            residentRank.getPermissionContext().getPermissionNodeForAction(residentRight), Tristate.TRUE);
                }
            }
        });
    }

}
