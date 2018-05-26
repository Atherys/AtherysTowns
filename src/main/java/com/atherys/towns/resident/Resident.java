package com.atherys.towns.resident;

import com.atherys.core.utils.UserUtils;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.resident.IResident;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.ResidentView;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

public class Resident implements IResident {

    private UUID uuid;

    private ITown town;

    private Date registered;
    private Date lastOnline;

    protected Resident(UUID uuid) {
        this.uuid = uuid;
        registered = new Date();
        lastOnline = new Date();
    }

    public static ResidentBuilder fromUUID(UUID uuid) {
        return new ResidentBuilder(uuid);
    }

    @Override
    public Optional<ITown> getTown() {
        return Optional.ofNullable(town);
    }

    @Override
    public void setTown(ITown town) {
        this.town = town;
    }

    @Override
    public Optional<? extends User> asUser() {
        return UserUtils.getUser(this.getUUID());
    }

    @Override
    public Date getRegistrationDate() {
        return registered;
    }

    public void setRegistrationDate(Date date) {
        this.registered = date;
    }

    @Override
    public Date getLastOnlineDate() {
        return lastOnline;
    }

    public void setLastOnlineDate(Date date) {
        this.lastOnline = date;
    }

    @Override
    public boolean isOnline() {
        return asUser().map(User::isOnline).orElse(false);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Optional<UniqueAccount> getBankAccount() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }

    @Override
    public ResidentView createView() {
        return new ResidentView(this);
    }
}
