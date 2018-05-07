package com.atherys.towns.base;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractAreaObject<T extends AreaObject> implements AreaObject {

    protected UUID uuid;
    private T parent;
    private UniqueAccount bank;

    protected AbstractAreaObject(UUID uuid) {
        this.uuid = uuid;
        createBank().ifPresent(uniqueAccount -> bank = uniqueAccount);
    }

    public UUID getUUID() {
        return uuid;
    }

    public Optional<UniqueAccount> getBank() {
        return Optional.ofNullable(bank);
    }

    public boolean deposit(Resident res, BigDecimal amount, Currency currency) {
        if (bank != null) {
            Optional<UniqueAccount> acc = res.getBank();
            if (acc.isPresent()) {
                UniqueAccount resAcc = acc.get();
                Cause cause = Cause.builder().append(res).build(EventContext.empty());

                resAcc.transfer(bank, currency, amount, cause);
                return true;
            }
        }
        return false;
    }

    public boolean withdraw(Resident res, BigDecimal amount, Currency currency) {
        if (bank != null) {
            Optional<UniqueAccount> acc = res.getBank();
            if (acc.isPresent()) {
                UniqueAccount resAcc = acc.get();
                Cause cause = Cause.builder().append(res).build(EventContext.empty());

                bank.transfer(resAcc, currency, amount, cause);
                return true;
            }
        }
        return false;
    }

    public Optional<T> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    private Optional<UniqueAccount> createBank() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }
}
