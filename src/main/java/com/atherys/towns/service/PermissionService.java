package com.atherys.towns.service;

import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.config.NationConfig;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Singleton
public class PermissionService {

    // this context represents the town that the player is currently a part of
    public static final String TOWN_CONTEXT_KEY = "atherystowns:town";

    // this context represents the town that the player is currently standing in, in the world
    public static final String TOWN_WORLD_CONTEXT_KEY = "atherystowns:world_town";

    // this context represents the nation that the player is currently a part of
    public static final String NATION_CONTEXT_KEY = "atherystowns:nation";

    // this context represents the nation that the player is currently standing in, in the world
    public static final String NATION_WORLD_CONTEXT_KEY = "atherystowns:world_nation";

    // Can't do database access in a context calculator, need to be fast.
    // For that purpose, cache players and their town/nation uuids
    // Crude, but effective.

    private Map<UUID, Context> playerTownContexts = new HashMap<>();

    private Map<UUID, Context> playerNationContexts = new HashMap<>();

    private Map<UUID, Context> playerWorldTownContexts = new HashMap<>();

    private Map<UUID, Context> playerWorldNationContexts = new HashMap<>();

    public PermissionService() {
    }

    /**
     * This method should ideally be called every time a player's town or nation changes.
     * Additionally, it should also be called when the player joins the server.
     * @param player The player whose town or nation has changed
     * @param resident The resident object for that player
     */
    public void updateContexts(@Nonnull Player player, @Nullable Resident resident) {
        if (resident != null) {
            Town town = resident.getTown();

            playerTownContexts.put(
                    player.getUniqueId(),
                    new Context(TOWN_CONTEXT_KEY, town.getId().toString())
            );

            String nation = town.getNation();

            if (nation != null) {
                playerNationContexts.put(
                        player.getUniqueId(),
                        new Context(NATION_CONTEXT_KEY, nation)
                );
            }
        } else {
            playerTownContexts.remove(player.getUniqueId());
            playerNationContexts.remove(player.getUniqueId());
        }
    }

    /**
     * This method should ideally be called every time a player enters or exists a town.
     * Additionally, it should also be called when the player joins the server.
     * @param player The player who has exited or entered a town
     * @param town The town ( if null, will remove town and nation contexts )
     */
    public void updateWorldContexts(@Nonnull Player player, @Nullable Town town) {
        if (town != null) {
            playerWorldTownContexts.put(
                    player.getUniqueId(),
                    new Context(TOWN_WORLD_CONTEXT_KEY, town.getId().toString())
            );

            String nation = town.getNation();

            if (nation != null) {
                playerWorldNationContexts.put(
                        player.getUniqueId(),
                        new Context(NATION_WORLD_CONTEXT_KEY, nation)
                );
            }
        } else {
            playerWorldTownContexts.remove(player.getUniqueId());
            playerWorldNationContexts.remove(player.getUniqueId());
        }
    }

    /**
     * Collect the contexts applicable for this player.
     * This includes normal as well as world contexts.
     * @param calculable The player
     * @param accumulator The set to accumulate the contexts
     */
    public void accumulateContexts(Player calculable, Set<Context> accumulator) {
        if (playerTownContexts.containsKey(calculable.getUniqueId())) {
            accumulator.add(playerTownContexts.get(calculable.getUniqueId()));
        }

        if (playerNationContexts.containsKey(calculable.getUniqueId())) {
            accumulator.add(playerNationContexts.get(calculable.getUniqueId()));
        }

        if (playerWorldTownContexts.containsKey(calculable.getUniqueId())) {
            accumulator.add(playerWorldTownContexts.get(calculable.getUniqueId()));
        }

        if (playerWorldNationContexts.containsKey(calculable.getUniqueId())) {
            accumulator.add(playerWorldNationContexts.get(calculable.getUniqueId()));
        }
    }

    public boolean isPermitted(Player player, Permission permission) {
        return player.hasPermission(permission.getId());
    }

    public void setPermission(Player player, Permission permission, Tristate tristate) {
        Set<Context> contexts = new HashSet<>();

        if (playerTownContexts.containsKey(player.getUniqueId())) {
            contexts.add(playerTownContexts.get(player.getUniqueId()));
        }

        if (playerNationContexts.containsKey(player.getUniqueId())) {
            contexts.add(playerNationContexts.get(player.getUniqueId()));
        }

        player.getSubjectData().setPermission(contexts, permission.getId(), tristate);
    }

    public void setWorldPermission(Player player, WorldPermission permission, Tristate tristate) {
        Set<Context> contexts = new HashSet<>();

        if (playerWorldTownContexts.containsKey(player.getUniqueId())) {
            contexts.add(playerWorldTownContexts.get(player.getUniqueId()));
        }

        if (playerWorldNationContexts.containsKey(player.getUniqueId())) {
            contexts.add(playerWorldNationContexts.get(player.getUniqueId()));
        }

        player.getSubjectData().setPermission(contexts, permission.getId(), tristate);
    }

    public
}
