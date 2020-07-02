package com.atherys.towns.util;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.facade.TownFacade;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Singleton
public class TaxTimer {

    @Inject
    TownsConfig config;

    @Inject
    TownFacade townFacade;

    @Inject
    TownRepository townRepository;

    public void init() {
        if (AtherysTowns.economyIsEnabled()) {
            Task.Builder taxTimer = Task.builder();
            taxTimer.delay(config.TAX_COLLECTION_INTERVAL, TimeUnit.MINUTES)
                    .interval(config.TAX_COLLECTION_INTERVAL, TimeUnit.MINUTES)
                    .execute(new TaxTimerTask())
                    .submit(AtherysTowns.getInstance());
        }
    }

    private class TaxTimerTask implements Consumer<Task> {
        @Override
        public void accept(Task task) {
            townRepository.getAll().stream().filter(town -> town.getNation().getBank() != null).forEach(town -> {
                Account nationBank = Economy.getAccount(town.getNation().getBank()).get();
                Account townBank = Economy.getAccount(town.getBank().toString()).get();

                double taxPaymentAmount = Math.floor(townBank.getBalance(config.DEFAULT_CURRENCY).doubleValue() * town.getNation().getTax());
                townBank.withdraw(config.DEFAULT_CURRENCY, BigDecimal.valueOf(taxPaymentAmount), Sponge.getCauseStackManager().getCurrentCause());
                nationBank.deposit(config.DEFAULT_CURRENCY, BigDecimal.valueOf(taxPaymentAmount), Sponge.getCauseStackManager().getCurrentCause());
                if (taxPaymentAmount > 0) {
                    townFacade.sendTownTaxMessage(town, taxPaymentAmount);
                }
            });
        }
    }
}
