package com.atherys.towns.util;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.facade.TownFacade;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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
            taxTimer.interval(15, TimeUnit.MINUTES)
                    .execute(TaxTimerTask())
                    .submit(AtherysTowns.getInstance());
        }
    }

    private Runnable TaxTimerTask() {
        return () -> townRepository.getAll().stream()
                .filter(town -> town.getNation() != null)
                .filter(town -> Duration.between(town.getLastTaxDate(), LocalDateTime.now())
                        .compareTo(config.TAX_COLLECTION_DURATION) > 0)
                .forEach(town -> {
                    Account nationBank = Economy.getAccount(town.getNation().getBank()).get();
                    Account townBank = Economy.getAccount(town.getBank().toString()).get();

                    double taxPaymentAmount = Math.floor(townBank.getBalance(config.DEFAULT_CURRENCY).doubleValue() * town.getNation().getTax());
                    townBank.withdraw(config.DEFAULT_CURRENCY, BigDecimal.valueOf(taxPaymentAmount), Sponge.getCauseStackManager().getCurrentCause());
                    nationBank.deposit(config.DEFAULT_CURRENCY, BigDecimal.valueOf(taxPaymentAmount), Sponge.getCauseStackManager().getCurrentCause());
                    town.setLastTaxDate(LocalDateTime.now());
                    if (taxPaymentAmount > 0) {
                        townFacade.sendTownTaxMessage(town, taxPaymentAmount);
                    }
                });
    }
}
