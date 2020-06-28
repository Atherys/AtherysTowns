package com.atherys.towns.facade;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.TownsCommandException;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;

public interface EconomyFacade {
    default Text getResultFeedback(ResultType resultType, Text success, Text notEnough, Text failure) throws TownsCommandException {
        switch (resultType) {
            case FAILED:
            case ACCOUNT_NO_SPACE:
            case CONTEXT_MISMATCH:
                throw new TownsCommandException(failure);
            case ACCOUNT_NO_FUNDS:
                throw new TownsCommandException(notEnough);
        }

        return success;
    }

    default void checkEconomyEnabled() throws TownsCommandException {
        if (!AtherysTowns.economyIsEnabled()) {
            throw TownsCommandException.economyNotEnabled();
        }
    }
}
