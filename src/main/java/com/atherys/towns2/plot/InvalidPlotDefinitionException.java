package com.atherys.towns2.plot;

import me.ryanhamshire.griefprevention.api.claim.ClaimResult;
import org.spongepowered.api.text.Text;

public class InvalidPlotDefinitionException extends Exception {

    private ClaimResult result;

    public InvalidPlotDefinitionException(ClaimResult claimResult) {
        super(claimResult.getMessage().orElse(Text.of("ClaimResult: ", claimResult.getResultType())).toPlain());
        this.result = claimResult;
    }

    public ClaimResult getResult() {
        return result;
    }
}
