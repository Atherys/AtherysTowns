package com.atherys.towns.service;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.persistence.ResidentRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.user.UserStorageService;

import java.math.BigDecimal;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Singleton
public class ResidentService {

    @Inject
    private ResidentRepository residentRepository;

    private EconomyService economyService = AtherysTowns.getInstance().getEconomyService().orElse(null);

    @Inject
    UserStorageService userStorageService;

    ResidentService() {
    }

    protected Resident getOrCreate(UUID playerUuid, String playerName) {
        Optional<Resident> resident = residentRepository.findById(playerUuid);

        if (resident.isPresent()) {
            return resident.get();
        } else {
            Resident newResident = new Resident();

            newResident.setId(playerUuid);
            newResident.setName(playerName);

            residentRepository.saveOne(newResident);

            return newResident;
        }
    }

    public Resident getOrCreate(User src) {
        return getOrCreate(src.getUniqueId(), src.getName());
    }

    public Optional<User> getUserFromResident(Resident resident) {
        return userStorageService.get(resident.getUniqueId());
    }

    public Optional<Player> getPlayerFromResident(Resident resident) {

        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            if ( onlinePlayer.getUniqueId().equals(resident.getId()) ) {
                return Optional.of(onlinePlayer);
            }
        }

        Optional<User> user = getUserFromResident(resident);
        return user.flatMap(User::getPlayer);
    }

    public Optional<UniqueAccount> getResidentBank(Resident resident) {
        return economyService.getOrCreateAccount(resident.getId());
    }

    public void addCurrency(Resident resident, BigDecimal amount, Currency currency, Cause cause) {
        getResidentBank(resident).ifPresent(account -> account.deposit(currency, amount, cause));
    }

    public void removeCurrency(Resident resident, BigDecimal amount, Currency currency, Cause cause) {
        getResidentBank(resident).ifPresent(account -> account.withdraw(currency, amount, cause));
    }

    public void transferCurrency(Resident resident, UUID destination, Currency currency, BigDecimal amount, Cause cause) {
        getResidentBank(resident).ifPresent(residentAccount -> {
            economyService.getOrCreateAccount(destination).ifPresent(destinationAccount -> {
                residentAccount.transfer(
                        destinationAccount,
                        currency,
                        amount,
                        cause
                );
            });
        });
    }

    public void transferCurrency(Resident resident, Town town, Currency currency, BigDecimal amount, Cause cause) {
        transferCurrency(resident, town.getId(), currency, amount, cause);
    }

    public void transferCurrency(Resident resident, Nation nation, Currency currency, BigDecimal amount, Cause cause) {
        transferCurrency(resident, nation.getId(), currency, amount, cause);
    }
}