package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class Plot implements DBObject {

    private Town town;

    private Claim claim;

    /**
     * Create a new plot, which immediately becomes part of a town
     *
     * @param town The town claiming the plot
     * @param pos1 The first corner
     * @param pos2 The second corner
     */
    public Plot(Town town, Vector3i pos1, Vector3i pos2) {
        ClaimResult result = Claim.builder()
                .type(ClaimType.BASIC)
                .world(town.getWorld())
                .bounds(pos1, pos2)
                .parent(town.getHomePlot().getClaim())
                .inherit(true)
                .build();

        if (result.successful() && result.getClaim().isPresent()) {
            this.claim = result.getClaim().get();
        }

        this.town = town;
    }

    public Plot(World world, UUID claimId) {
        GriefPrevention.getApi().getClaimManager(world).getClaimByUUID(claimId).ifPresent(claim -> this.claim = claim);
    }

    /**
     * Creates a new Plot, which is to be used as the Home Plot for a new Town
     *
     * @param world The world
     * @param pos1 First corner
     * @param pos2 Second corner
     */
    public Plot(World world, Vector3i pos1, Vector3i pos2) {
        ClaimResult result = Claim.builder()
                .type(ClaimType.BASIC)
                .world(world)
                .bounds(pos1, pos2)
                .build();

        if (result.successful() && result.getClaim().isPresent()) {
            this.claim = result.getClaim().get();
        }
    }

    public Claim getClaim() {
        return claim;
    }

    public Town getTown() {
        return town;
    }

    @Override
    public UUID getUniqueId() {
        return claim.getUniqueId();
    }
}
