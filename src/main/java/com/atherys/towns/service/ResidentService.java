package com.atherys.towns.service;

import com.atherys.core.economy.Economy;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Resident;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.persistence.ResidentRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.user.UserStorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class ResidentService {

    @Inject
    private ResidentRepository residentRepository;

    @Inject
    private UserStorageService userStorageService;

    ResidentService() {
    }

    public Resident createFakeResident(String name) {
        Resident res = getOrCreate(UUID.randomUUID(), name, true);
        res.setLastLogin(LocalDateTime.now());
        return res;
    }

    public Resident getOrCreate(UUID playerUuid, String playerName, boolean isFake) {
        Optional<Resident> resident = residentRepository.findById(playerUuid);

        if (resident.isPresent()) {
            Resident res = resident.get();
            res.setName(playerName);
            res.setLastLogin(LocalDateTime.now());
            res.setFake(isFake);
            return res;
        } else {
            Resident newResident = new Resident();

            newResident.setId(playerUuid);
            newResident.setName(playerName);
            newResident.setRegisteredOn(LocalDateTime.now());
            newResident.setLastTownSpawn(LocalDateTime.now().minus(1, ChronoUnit.YEARS));

            residentRepository.saveOne(newResident);

            return newResident;
        }
    }

    public Resident getOrCreate(User src) {
        return getOrCreate(src.getUniqueId(), src.getName(), false);
    }

    public Optional<User> getUserFromResident(Resident resident) {
        return userStorageService.get(resident.getUniqueId());
    }

    public Optional<Player> getPlayerFromResident(Resident resident) {

        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(resident.getId())) {
                return Optional.of(onlinePlayer);
            }
        }

        Optional<User> user = getUserFromResident(resident);
        return user.flatMap(User::getPlayer);
    }

    public void addResidentFriend(Resident resident, Resident friend) {
        resident.addFriend(friend);
        residentRepository.saveOne(resident);
    }

    public void removeResidentFriend(Resident resident, Resident friend) {
        resident.removeFriend(friend);
        residentRepository.saveOne(resident);
    }

    public void setLastTownSpawn(Resident resident, LocalDateTime time) {
        resident.setLastTownSpawn(time);
        residentRepository.saveOne(resident);
    }

    public void setLastLogin(Resident resident, LocalDateTime time) {
        resident.setLastLogin(time);
        residentRepository.saveOne(resident);
    }

    public Optional<UniqueAccount> getResidentBank(Resident resident) {
        return Economy.getAccount(resident.getId());
    }

    public void addCurrency(Resident resident, BigDecimal amount, Currency currency, Cause cause) {
        Economy.addCurrency(resident.getId(), currency, amount, cause);
    }

    public void removeCurrency(Resident resident, BigDecimal amount, Currency currency, Cause cause) {
        Economy.removeCurrency(resident.getId(), currency, amount, cause);
    }

    public void transferCurrency(Resident source, Town destination, Currency currency, BigDecimal amount, Cause cause) {
        Economy.transferCurrency(source.getId(), destination.getBank(), currency, amount, cause);
    }

    public void transferCurrency(Resident source, Nation destination, Currency currency, BigDecimal amount, Cause cause) {
        Economy.transferCurrency(source.getId(), destination.getBank(), currency, amount, cause);
    }

    public void transferCurrency(Town source, Resident destination, Currency currency, BigDecimal amount, Cause cause) {
        Economy.transferCurrency(source.getBank(), destination.getId(), currency, amount, cause);
    }

    public void transferCurrency(Nation source, Resident destination, Currency currency, BigDecimal amount, Cause cause) {
        Economy.transferCurrency(source.getBank(), destination.getId(), currency, amount, cause);
    }

    public boolean isResidentTownLeader(Resident resident, Town town) {
        return town.getLeader() != null && town.getLeader().getId().equals(resident.getId());
    }

    public void grantTownRole(Resident resident, String role) {
        resident.getTownRoleIds().add(role);
        residentRepository.saveOne(resident);
    }

    public void grantNationRole(Resident resident, String role) {
        resident.getNationRoleIds().add(role);
        residentRepository.saveOne(resident);
    }

    public void removeTownRole(Resident resident, String role) {
        resident.getTownRoleIds().remove(role);
        residentRepository.saveOne(resident);
    }

    public void removeNationRole(Resident resident, String role) {
        resident.getNationRoleIds().remove(role);
        residentRepository.saveOne(resident);
    }
}