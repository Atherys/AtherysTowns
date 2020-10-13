package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.TaxService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

@Singleton
public class TaxFacade {

    @Inject
    private TownsConfig config;

    @Inject
    private TaxService taxService;

    @Inject
    private TownService townService;

    private Task townTaxTask;

    public void init() {
        if (AtherysTowns.economyIsEnabled() && config.TAXES.IS_ENABLED) {
            Task.Builder taxTimer = Task.builder();
            townTaxTask = taxTimer.interval(config.TAXES.TAX_TIMER_INTERVAL.toMinutes(), TimeUnit.MINUTES)
                    .execute(this::taxTowns)
                    .submit(AtherysTowns.getInstance());
        }
    }

    public void taxTowns() {
        Set<Town> taxableTowns = taxService.getTaxableTowns();

        Set<Town> townsToRemove = new HashSet<>();

        for (Town town : taxableTowns) {
            TownsMessagingFacade townsMsg = AtherysTowns.getInstance().getTownsMessagingService();
            double taxPaymentAmount = Math.floor(taxService.getTaxAmount(town));
            Account townBank = Economy.getAccount(town.getBank().toString()).get();
            double townBalance = townBank.getBalance(config.DEFAULT_CURRENCY).doubleValue();
            boolean hasMetMaximumTaxFailures = town.getTaxFailedCount() >= config.TAXES.MAX_TAX_FAILURES;
            boolean hasEnoughMoney = taxPaymentAmount <= townBalance;

            if (!hasEnoughMoney && hasMetMaximumTaxFailures) {
                townsMsg.broadcastTownError(town, Text.of("Failure to pay taxes has resulted in your town being ruined!"));
                townsToRemove.add(town);
                continue;
            }

            if (!hasEnoughMoney) {
                townsMsg.broadcastTownError(town, Text.of("You have failed to pay your taxes! If not paid by next tax cycle your town will be ruined!" +
                        " Town features have been limited until paid off."));
                taxService.payTaxes(town, townBalance);
                townService.addTownDebt(town, (taxPaymentAmount - townBalance));
                taxService.setTaxesPaid(town, false);
                continue;
            }

            if (town.getTaxFailedCount() > 0) {
                townsMsg.broadcastTownInfo(town, Text.of("You have paid what you owe, all town features have been restored."));
                taxService.setTaxesPaid(town, true);
                taxService.payTaxes(town, taxPaymentAmount);
                town.setLastTaxDate(LocalDateTime.now());
                continue;
            }

            townsMsg.broadcastTownInfo(town, Text.of("Paid ", GOLD, config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(taxPaymentAmount)), DARK_GREEN, " to ", GOLD,
                    town.getNation().getName(), DARK_GREEN, " in taxes."));
            taxService.payTaxes(town, taxPaymentAmount);
            town.setLastTaxDate(LocalDateTime.now());
        }

        townsToRemove.forEach(townService::removeTown);
    };

}
