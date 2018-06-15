package com.atherys.towns2.plot;

import com.atherys.towns2.base.LocationContainer;
import com.atherys.towns2.town.Town;
import com.flowpowered.math.vector.Vector3i;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * A wrapper class around GriefPrevention's {@link Claim} with a {@link Town} parent object.
 */
public class Plot implements LocationContainer<World>, Identifiable {

    private UUID uuid;

    private Town town;

    private Claim claim;

    public Plot(World world, Vector3i from, Vector3i to) throws InvalidPlotDefinitionException {

        ClaimResult claimResult = Claim.builder()
                .bounds(from, to)
                .world(world)
                .type(ClaimType.BASIC)
                .build();

        if (!claimResult.successful()) throw new InvalidPlotDefinitionException(claimResult);
        else {
            this.claim = claimResult.getClaims().get(0);
            this.uuid = claim.getUniqueId();
        }
    }

    /**
     * A simple factory method for creating a {@link Plot}.
     * @param town The {@link Town} parent.
     * @param from First Corner
     * @param to Second Corner
     * @return A {@link PlotClaimResult}.
     */
    public static PlotClaimResult of(Town town, Vector3i from, Vector3i to) {
        Plot result;

        try {
            result = new Plot(town.getExtent(), from, to);
            result.setTown(town);
        } catch (InvalidPlotDefinitionException e) {
            return new PlotClaimResult(e.getResult(), null);
        }

        return new PlotClaimResult(null, result);
    }

    @Override
    @Nonnull
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public World getExtent() {
        return claim.getWorld();
    }

    @Override
    public boolean contains(Location<World> location) {
        return claim.contains(location);
    }

    @Override
    public int getArea() {
        return claim.getArea();
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Text getName() {
        return claim.getData().getName().orElse(Text.EMPTY);
    }

    public void setName(Text name) {
        claim.getData().setName(name);
    }

    public Claim getClaim() {
        return claim;
    }
}
