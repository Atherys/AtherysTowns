package com.atherys.towns.resident;

import com.atherys.core.utils.UserUtils;
import com.atherys.core.views.Viewable;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.base.TownsObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.NationRank;
import com.atherys.towns.permissions.ranks.NationRanks;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.town.Town;
import com.atherys.towns.views.ResidentView;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class Resident implements TownsObject, Viewable<ResidentView> {

    private UUID uuid;

    @Nullable
    private Town town;
    @Nullable
    private TownRank townRank;
    @Nullable
    private NationRank nationRank;

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

    public String getName() {
        if (!getUser().isPresent()) {
            return AtherysTowns.getConfig().TOWN.NPC_NAME;
        }
        return getUser().get().getName();
    }

    @Override
    public void setName(String name) {

    }

    // return user object regardless if player is online or not
    private Optional<? extends User> getUser() {
        return UserUtils.getUser(uuid);
    }

    // returns player object IF player is online
    public Optional<Player> getPlayer() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public boolean isOnline() {
        return getPlayer().isPresent();
    }

    public Optional<Town> getTown() {
        if (town != null) {
            return Optional.of(town);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Nation> getNation() {
        return NationManager.getInstance().getByResident(this);
    }

    public void setTown(Town town, TownRank rank) {
        this.town = town;
        setTownRank(rank);
        setNationRank(rank.getDefaultNationRank());
    }

    public void updatePermissions() {
        setTownRank(getTownRank());
        setNationRank(getNationRank());
    }

    public TownRank getTownRank() {
        return townRank == null ? TownRanks.NONE : townRank;
    }

    public Resident setTownRank(TownRank townRank) {
        getTownRank().updatePermissions(this.uuid, townRank);
        this.townRank = townRank;
        return this;
    }

    public NationRank getNationRank() {
        return nationRank == null ? NationRanks.NONE : nationRank;
    }

    public void setNationRank(NationRank nationRank) {
        getNationRank().updatePermissions(this.uuid, nationRank);
        this.nationRank = nationRank;
    }

    public Date getRegisteredDate() {
        return registered;
    }

    protected void setRegisteredDate(Date registered) {
        this.registered = registered;
    }

    public Date getLastOnlineDate() {
        return lastOnline;
    }

    public void updateLastOnline() {
        this.lastOnline = new Date();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public Optional<UniqueAccount> getBank() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }

    @Override
    public ResidentView createView() {
        return new ResidentView(this);
    }
}
