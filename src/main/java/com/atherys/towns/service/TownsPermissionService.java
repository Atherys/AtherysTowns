package com.atherys.towns.service;

import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.nation.NationPermission;
import com.atherys.towns.api.permission.town.TownPermission;
import com.atherys.towns.api.permission.world.WorldPermission;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Singleton
public class TownsPermissionService {

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

    public TownsPermissionService() {
    }

    /**
     * This method should ideally be called every time a player's town or nation changes.
     * Additionally, it should also be called when the player joins the server.
     *
     * @param player   The player whose town or nation has changed
     * @param resident The resident object for that player
     */
    public void updateContexts(@Nonnull User player, @Nullable Resident resident) {
        if (resident != null) {
            Town town = resident.getTown();

            playerTownContexts.put(
                    player.getUniqueId(),
                    new Context(TOWN_CONTEXT_KEY, town.getId().toString())
            );

            Nation nation = town.getNation();

            if (nation != null) {
                playerNationContexts.put(
                        player.getUniqueId(),
                        new Context(NATION_CONTEXT_KEY, nation.getId().toString())
                );
            }
        } else {
            playerTownContexts.remove(player.getUniqueId());
            playerNationContexts.remove(player.getUniqueId());
        }
    }

    public Set<Context> getContextsForTown(Town town) {
        Context townContext = new Context(TOWN_CONTEXT_KEY, town.getId().toString());
        Context nationContext = town.getNation() == null ? null : new Context(NATION_CONTEXT_KEY, town.getNation().getId().toString());

        if (nationContext != null) {
            return ImmutableSet.of(townContext, nationContext);
        }
        return Collections.singleton(townContext);
    }

    public Set<Context> getContextForNation(Nation nation) {
        return Collections.singleton(new Context(NATION_CONTEXT_KEY, nation.getId().toString()));
    }

    /**
     * This method should ideally be called every time a player enters or exits a town.
     * Additionally, it should also be called when the player joins the server.
     *
     * @param player The player who has exited or entered a town
     * @param town   The town ( if null, will remove town and nation contexts )
     */
    public void updateWorldContexts(@Nonnull Player player, @Nullable Town town) {
        if (town != null) {
            playerWorldTownContexts.put(
                    player.getUniqueId(),
                    new Context(TOWN_WORLD_CONTEXT_KEY, town.getId().toString())
            );

            Nation nation = town.getNation();

            if (nation != null) {
                playerWorldNationContexts.put(
                        player.getUniqueId(),
                        new Context(NATION_WORLD_CONTEXT_KEY, nation.getId().toString())
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
     *
     * @param calculable  The player
     * @param accumulator The set to accumulate the contexts
     */
    public void accumulateContexts(Subject calculable, Set<Context> accumulator) {
        Optional<CommandSource> commandSource = calculable.getCommandSource();
        if (commandSource.isPresent() && commandSource.get() instanceof Player) {
            Player player = (Player) commandSource.get();

            if (playerTownContexts.containsKey(player.getUniqueId())) {
                accumulator.add(playerTownContexts.get(player.getUniqueId()));
            }

            if (playerNationContexts.containsKey(player.getUniqueId())) {
                accumulator.add(playerNationContexts.get(player.getUniqueId()));
            }

            if (playerWorldTownContexts.containsKey(player.getUniqueId())) {
                accumulator.add(playerWorldTownContexts.get(player.getUniqueId()));
            }

            if (playerWorldNationContexts.containsKey(player.getUniqueId())) {
                accumulator.add(playerWorldNationContexts.get(player.getUniqueId()));
            }
        }
    }

    public boolean isPermitted(Player player, Permission permission) {
        return player.hasPermission(permission.getId());
    }

    /**
     * Sets a permission in the context of a player's current nation.
     */
    public void setNationPermission(Player player, NationPermission permission, Tristate tristate) {
        if (playerNationContexts.containsKey(player.getUniqueId())) {
            Set<Context> nationContext = Collections.singleton(playerNationContexts.get(player.getUniqueId()));
            player.getSubjectData().setPermission(nationContext, permission.getId(), tristate);
        }
    }

    /**
     * Sets a permission in the context of a player's current town.
     */
    public void setTownPermission(Player player, TownPermission permission, Tristate tristate) {
        if (playerTownContexts.containsKey(player.getUniqueId())) {
            Set<Context> townContext = Collections.singleton(playerTownContexts.get(player.getUniqueId()));
            player.getSubjectData().setPermission(townContext, permission.getId(), tristate);
        }
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

    public void clearPermissions(User user, Town town) {
        clearPermissions(user, getContextsForTown(town));
    }

    public void clearPermissions(User user, Nation nation) {
        clearPermissions(user, getContextForNation(nation));
    }

    public void clearPermissions(Subject subject, Set<Context> contexts) {
        subject.getSubjectData().clearParents(contexts);
        subject.getSubjectData().clearPermissions(contexts);
    }
}
