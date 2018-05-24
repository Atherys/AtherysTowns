package com.atherys.towns.api;

import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankHolder {

    Optional<UniqueAccount> getBankAccount();

    default void withdrawFrom(BankHolder holder, BigDecimal amount) {
        holder.transferTo(this, amount);
    }

    default void withdrawFrom(BankHolder holder, BigDecimal amount, Currency currency) {
        holder.transferTo(this, amount, currency);
    }

    default void depositInto(BankHolder holder, BigDecimal amount) {
        this.transferTo(holder, amount);
    }

    default void depositInto(BankHolder holder, BigDecimal amount, Currency currency) {
        this.transferTo(holder, amount, currency);
    }

    default void transferTo(BankHolder holder, BigDecimal amount) {
        AtherysTowns.getInstance().getEconomyService().ifPresent( service -> {
            transferTo(holder, amount, service.getDefaultCurrency());
        });
    }

    default void transferTo(BankHolder holder, BigDecimal amount, Currency currency) {
        getBankAccount().ifPresent(thisAccount -> {
            holder.getBankAccount().ifPresent(transferAccount -> {
                thisAccount.transfer(transferAccount, currency, amount, Cause.builder().build(Sponge.getCauseStackManager().getCurrentContext()));
            });
        });
    }
}
