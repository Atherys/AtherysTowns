package com.atherys.towns2.plot;

import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import me.ryanhamshire.griefprevention.api.claim.ClaimResultType;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A wrapper class around GriefPrevention's {@link ClaimResult} which contains a {@link Plot}
 */
public class PlotClaimResult {

    private ClaimResult claimResult;

    private Plot plot;

    PlotClaimResult(@Nullable ClaimResult claimResult, @Nullable Plot plot) {
        this.claimResult = claimResult;
        this.plot = plot;
    }

    /**
     * Retrieve the raw {@link ClaimResult}, if present. If not present, this means {@link PlotClaimResult#success()} returns true.
     *
     * @return An optional containing the ClaimResult.
     */
    public Optional<ClaimResult> getResult() {
        return Optional.of(claimResult);
    }

    /**
     * Checks whether or not this PlotClaimResult was successful.
     *
     * @return true if the underlying {@link ClaimResult} was null, or successful;
     */
    public boolean success() {
        return claimResult == null || claimResult.successful();
    }

    /**
     * Retrieves the message from the underlying {@link ClaimResult}
     *
     * @return The underlying text message, or an empty one, if successful.
     */
    public Text message() {
        return claimResult == null ? Text.EMPTY : claimResult.getMessage().orElse(Text.of("ClaimResult: ", claimResult.getResultType()));
    }

    /**
     * Returns the underlying {@link ClaimResultType}.
     *
     * @return The underlying result type, of {@link ClaimResultType#SUCCESS} if the ClaimResult is null.
     */
    public ClaimResultType type() {
        return claimResult == null ? ClaimResultType.SUCCESS : claimResult.getResultType();
    }

    /**
     * Retrieves the {@link Plot} that was to be created.
     *
     * @return An optional containing the plot if successful, or an empty optional if not.
     */
    public Optional<Plot> getPlot() {
        return Optional.ofNullable(plot);
    }
}
