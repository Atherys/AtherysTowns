package com.atherys.towns.service;

import com.atherys.core.economy.Economy;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.TownRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class TaxService {

    @Inject
    private TownsConfig config;

    @Inject
    private TownService townService;

    @Inject
    private TownRepository townRepository;

    public double getTaxAmount(Town town) {
        double pvpMultiplier = town.isPvpEnabled() ? 1.0 : config.TAXES.PVP_TAX_MODIFIER;
        double nationMultiplier = town.getNation() == null ? 1.0 : town.getNation().getTax();

        return nationMultiplier * pvpMultiplier * (calcBaseTax(town) + calcResidentTax(town) + calcAreaTax(town)) + town.getDebt();
    }

    /**
     * A town is taxed for each active resident currently residing in the town, per-resident-tax amount each
     */
    public double calcResidentTax(Town town) {
        long numberOfActiveResidents = town.getResidents().stream()
                .filter(resident -> Duration.between(resident.getLastLogin(), LocalDateTime.now()).compareTo(config.TAXES.INACTIVE_DURATION) < 0)
                .count();

        return config.TAXES.PER_RESIDENT_TAX * numberOfActiveResidents;
    }

    /**
     * Tax each claimed block of land with the per-block-area-tax
     */
    public double calcAreaTax(Town town) {
        int townArea = townService.getTownSize(town);

        return (config.TAXES.PER_BLOCK_AREA_TAX * townArea) + calcOversizeAreaTax(town, townArea);
    }

    /**
     * A town is considered oversize if it has more land claimed than its allowed max size.
     * The oversize area tax modifier is applied to the amount of area that the town is over its limit.
     *
     * Example:
     * If a town has claimed 3000/2000 blocks, 2000 blocks are taxed normally, and the remaining 1000 have the oversize-area-tax-modifier applied
     */
    public double calcOversizeAreaTax(Town town, int townArea) {
        int oversizeArea = townArea > town.getMaxSize() ? townArea - town.getMaxSize() : 0;
        return config.TAXES.OVERSIZE_AREA_TAX_MODIFIER * config.TAXES.PER_BLOCK_AREA_TAX * oversizeArea;
    }

    /**
     * Base tax is taken directly from the tax configuration
     */
    public double calcBaseTax(Town town) {
        return config.TAXES.BASE_TAX;
    }

    public void setTaxesPaid(Town town, boolean paid) {
        if (!paid) {
            townService.addFailedTaxOccurrence(town);
            townService.setTownPvp(town, true);
        } else {
            townService.setTownTaxFailCount(town, 0);
            townService.setTownDebt(town, 0);
        }
    }

    public Optional<TransferResult> payTaxes(Town town, double amount) {
        Cause cause = Sponge.getCauseStackManager().getCurrentCause();
        return Economy.transferCurrency(town.getBank().toString(), town.getNation().getBank().toString(), config.DEFAULT_CURRENCY, BigDecimal.valueOf(amount), cause);
    }

    private boolean isTaxTime(Town town) {
        return Duration.between(town.getLastTaxDate(), LocalDateTime.now())
                .compareTo(config.TAXES.TAX_COLLECTION_DURATION) > 0;
    }

    public boolean isTaxable(Town town) {
        return (town.getNation() != null && !town.getNation().getCapital().equals(town));
    }

    public Set<Town> getTaxableTowns() {
        return townRepository.getAll().stream()
                .filter(this::isTaxable)
                .filter(this::isTaxTime)
                .collect(Collectors.toSet());
    }
}
