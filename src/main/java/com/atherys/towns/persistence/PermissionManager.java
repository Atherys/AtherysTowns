package com.atherys.towns.persistence;

import com.atherys.towns.PermsCache;
import com.atherys.towns.model.Resident;
import com.atherys.towns.model.permission.*;
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

    public boolean hasPermission(ResidentAction action, PermissionContext context, Resident resident) {
        Optional<Player> player = resident.getPlayer();
        if (!player.isPresent()) {
            throw new RuntimeException("Player offline exception");
        }
        return player.get().hasPermission(context.getPermissionNodeForAction(action));
    }

    public void updatePermsCache(PermissionContext permissionContext, Resident resident) {
        PermsCache permsCache = resident.getPermsCache();
        permsCache.mayBuildBlocks = hasPermission(ResidentRights.BUILD, permissionContext, resident);
        permsCache.mayDestroyBlocks = hasPermission(ResidentRights.DESTROY, permissionContext, resident);
        permsCache.mayDamageEntities = hasPermission(ResidentRights.DAMAGE_ENTITY, permissionContext, resident);
        permsCache.mayDamagePlayers = hasPermission(ResidentRights.DAMAGE_PLAYER, permissionContext, resident);
        permsCache.mayInteractWithDoors = hasPermission(ResidentRights.INTERACT_WITH_DOORS, permissionContext, resident);
        permsCache.mayInteractWithRedstoneStuff = hasPermission(ResidentRights.INTERACT_WITH_OTHER_WEIRD_STUFF, permissionContext, resident);

        permsCache.context = permissionContext;
    }

}
