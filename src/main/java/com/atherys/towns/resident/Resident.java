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
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Resident implements TownsObject, Viewable<ResidentView> {

    private UUID uuid;

    @Nullable private Town town;
    @Nullable private TownRank townRank;
    @Nullable private NationRank nationRank;

    private long unixRegisterDateSeconds = 0;
    private long unixLastOnlineSeconds = 0;

    protected Resident( UUID uuid) {
        this.uuid = uuid;
        unixRegisterDateSeconds = System.currentTimeMillis() / 1000L;
        unixLastOnlineSeconds = System.currentTimeMillis() / 1000L;
    }

    public static ResidentBuilder fromUUID (UUID uuid ) {
        return new ResidentBuilder(uuid);
    }

    public String getName() {
        if ( !getUser().isPresent() ) return AtherysTowns.getConfig().TOWN.NPC_NAME;
        return getUser().get().getName();
    }

    // return user object regardless if player is online or not
    private Optional<? extends User> getUser() {
        return UserUtils.getUser(uuid);
    }

    private CompletableFuture<GameProfile> getProfile() {
        return Sponge.getServer().getGameProfileManager().get( uuid );
    }

    // returns player object IF player is online
    public Optional<Player> getPlayer() {
        for ( Player player : Sponge.getServer().getOnlinePlayers() ) {
            if ( player.getUniqueId().equals(uuid) ) return Optional.of(player);
        }
        return Optional.empty();
    }

    public boolean isOnline() {
        return getPlayer().isPresent();
    }

    public Optional<Town> getTown() {
        if ( town != null ) return Optional.of(town);
        else return Optional.empty();
    }

    public Optional<Nation> getNation() {
        return NationManager.getInstance().getByResident(this);
    }

    public void setTown( Town town, TownRank rank ) {
        this.town = town;
        setTownRank( rank );
        setNationRank( rank.getDefaultNationRank() );
    }

    public void updatePermissions() {
        setTownRank( getTownRank() );
        setNationRank( getNationRank() );
    }

    public TownRank getTownRank() {
        return townRank == null ? TownRanks.NONE : townRank;
    }

    public NationRank getNationRank() {
        return nationRank == null ? NationRanks.NONE : nationRank;
    }

    public Resident setTownRank ( TownRank townRank ) {
        CompletableFuture<GameProfile> user = this.getProfile();
        // TODO: This needs to be fixed.
        if ( user.isPresent() ) {
            getTownRank().removePermissions( user.get() );
            townRank.addPermissions( user.get() );
        }
        this.townRank = townRank;
        return this;
    }

    public long getRegisteredSeconds() { return unixRegisterDateSeconds; }

    public long getLastOnlineSeconds() { return unixLastOnlineSeconds; }

    public Date getRegisterDate() {
        return Date.from( Instant.ofEpochSecond(unixRegisterDateSeconds) );
    }

    public Date getLastOnlineDate() {
        return Date.from( Instant.ofEpochSecond(unixLastOnlineSeconds) );
    }

    public void updateLastOnline() {
        this.unixLastOnlineSeconds = System.currentTimeMillis() / 1000L;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setName(String name) {

    }

    public Optional<UniqueAccount> getBank() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }

    public void setNationRank ( NationRank nationRank ) {
        Optional<? extends User> user = this.getUser();
        if ( user.isPresent() ) {
            getNationRank().removePermissions( user.get() );
            nationRank.addPermissions( user.get() );
        }
        this.nationRank = nationRank;
    }

    public void setRegisteredTimestamp(long registeredTimestamp) {
        this.unixRegisterDateSeconds = registeredTimestamp;
    }

    @Override
    public Optional<ResidentView> createView() {
        return Optional.of( new ResidentView(this) );
    }
}
