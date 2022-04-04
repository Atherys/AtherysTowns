package com.atherys.towns.facade;

import com.atherys.core.economy.Economy;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.TaxService;
import com.atherys.towns.service.TownService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.spongepowered.api.text.format.TextColors.*;

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
        if (config.TAXES.IS_ENABLED) {
            townTaxTask = Task.builder()
                    .execute(this::taxTowns)
                    .interval(config.TAXES.TAX_TIMER_INTERVAL.toMinutes(), TimeUnit.MINUTES)
                    .submit(AtherysTowns.getInstance());
        }
    }

    public void taxTowns() {
        Set<Town> townsToRemove = new HashSet<>();
        TownsMessagingFacade townsMsg = AtherysTowns.getInstance().getTownsMessagingService();

        for (Town town : taxService.getTaxableTowns()) {
            double taxPaymentAmount = Math.floor(taxService.getTaxAmount(town));
            double voidedAmount = taxPaymentAmount * config.TAXES.VOID_RATE;
            taxPaymentAmount -= voidedAmount;

            Account townBank = Economy.getAccount(town.getBank().toString()).get();
            double townBalance = townBank.getBalance(config.DEFAULT_CURRENCY).doubleValue();

            if (townBalance < taxPaymentAmount + voidedAmount) {
                if (town.getTaxFailedCount() >= config.TAXES.MAX_TAX_FAILURES) {
                    townsMsg.broadcastTownError(town, Text.of("Failure to pay taxes has resulted in your ",
                            "town being ruined!"));
                    townsToRemove.add(town);
                } else {
                    int cycles = config.TAXES.MAX_TAX_FAILURES - town.getTaxFailedCount();
                    townsMsg.broadcastTownError(town, Text.of("Your town has failed to pay its taxes! If not paid fully within the next " +
                            cycles, " tax cycle" + (cycles == 1 ? "" : "s"), " your town will be ruined! Town features have been limited until paid off."));

                    voidedAmount = townBalance * config.TAXES.VOID_RATE;
                    taxPaymentAmount = townBalance - voidedAmount;
                    taxService.payTaxes(town, townBalance, voidedAmount);
                    townService.addTownDebt(town, (taxPaymentAmount + voidedAmount - townBalance - town.getDebt()));
                    taxService.setTaxesPaid(town, false);
                }
            } else {
                townsMsg.broadcastTownInfo(town, Text.of("Paid ", GOLD,
                        config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(taxPaymentAmount)), DARK_GREEN, " to ",
                        GOLD, town.getNation().getName(), DARK_GREEN, " in taxes."));

                taxService.payTaxes(town, taxPaymentAmount, voidedAmount);
                taxService.setTaxesPaid(town, true);
            }

            town.setLastTaxDate(LocalDateTime.now());
        }

        townsToRemove.forEach(townService::removeTown);
    }


    public Text renderTax(Town town) {
        Text.Builder taxText = Text.builder();

        if (!config.TAXES.IS_ENABLED || !taxService.isTaxable(town)) {
            return taxText.build();
        }

        Text.Builder nextPaymentText = Text.builder();

        double townTotalTax = taxService.getTaxAmount(town);
        double townTotalTaxLessDebt = townTotalTax - town.getDebt();

        nextPaymentText.append(
                Text.of(DARK_GREEN, "Next Tax Payment: ", GOLD,
                        config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(townTotalTax)),
                        Text.NEW_LINE));

        Text.Builder hoverText = Text.builder();
        hoverText.append(Text.of(DARK_GREEN, "Base Tax: ", GOLD,
                config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(taxService.calcBaseTax(town))), Text.NEW_LINE));

        // Calculate Nation Tax/Rebate
        double nationMultiplier = town.getNation() == null ? 1.0 : town.getNation().getTax();
        if (nationMultiplier != 1.0) {
            double nationTax = townTotalTaxLessDebt - (townTotalTaxLessDebt / nationMultiplier);
            String taxType = nationTax > 0 ? "Tax" : "Rebate";
            hoverText.append(Text.of(DARK_GREEN, "Nation ", taxType, ": ", GOLD,
                    config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(nationTax)), Text.NEW_LINE));
        }

        hoverText.append(Text.of(DARK_GREEN, "Area Tax: ", GOLD,
                config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(taxService.calcAreaTax(town))), Text.NEW_LINE));
        hoverText.append(Text.of(DARK_GREEN, "Resident Tax: ", GOLD,
                config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(taxService.calcResidentTax(town))), Text.NEW_LINE));

        // Calculate the PVP
        double pvpMultiplier = town.isPvpEnabled() ? 1.0 : config.TAXES.PVP_TAX_MODIFIER;
        if (pvpMultiplier != 1.0) {
            double pvpPenalty = townTotalTaxLessDebt - (townTotalTaxLessDebt / pvpMultiplier);
            hoverText.append(Text.of(DARK_GREEN, "PVP Penalty: ", GOLD,
                    config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(pvpPenalty)), Text.NEW_LINE));
        }

        if (town.getDebt() > 0) {
            hoverText.append(Text.of(DARK_GREEN, "Debt: ", GOLD,
                    config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(town.getDebt())), Text.NEW_LINE));
        }

        // Workout when the next tax-cycle will occur for this town
        hoverText.append(Text.of(DARK_GREEN, "Due: ", GOLD));
        LocalDateTime next = town.getLastTaxDate().plus(config.TAXES.TAX_COLLECTION_DURATION);
        Duration until = Duration.between(LocalDateTime.now(), next);

        if (until.isNegative()) {
            hoverText.append(Text.of(RED, "Now"));
        } else {
            String format = "H'h 'm'm 's's'";
            if (until.toDays() > 0) {
                format = "d'd '" + format;
            }
            hoverText.append(Text.of(GOLD, DurationFormatUtils.formatDuration(until.toMillis(), format)));
        }

        nextPaymentText.onHover(TextActions.showText(hoverText.build()));

        taxText.append(nextPaymentText.build());

        if (town.getDebt() > 0) {
            taxText.append(Text.of(DARK_GREEN, "Debt Owed: ", RED,
                    config.DEFAULT_CURRENCY.format(BigDecimal.valueOf(town.getDebt())), Text.NEW_LINE));
        }

        return taxText.build();
    }

}
