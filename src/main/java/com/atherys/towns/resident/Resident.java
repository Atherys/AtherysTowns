package com.atherys.towns.resident;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.TownsObject;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.*;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.Question;
import com.atherys.towns.utils.UserUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class Resident implements TownsObject {

    public static Resident.Builder fromUUID ( UUID uuid ) {
        return new Builder(uuid);
    }

    public static class Builder {

        Resident res;

        Builder ( UUID uuid ) {
            res = new Resident( uuid );
        }

        public Resident.Builder town ( Town town, TownRank2 rank ) {
            if ( town != null ) {
                res.setTown(town, rank);
            }
            return this;
        }

        public Resident.Builder townRank ( TownRank2 rank ) {
            res.setTownRank(rank);
            return this;
        }

        public Resident.Builder nationRank ( NationRank2 rank ) {
            res.setNationRank(rank);
            return this;
        }

        public Resident.Builder registerTimestamp ( long time ) {
            res.setRegisteredTimestamp(time);
            return this;
        }

        public Resident.Builder updateLastOnline () {
            res.updateLastOnline();
            return this;
        }

        public Resident build() {
            ResidentManager.getInstance().add(res.getUUID(),res);
            return res;
        }
    }


    private UUID uuid;

    @Nullable private Town town;
    @Nullable private TownRank2 townRank;
    @Nullable private NationRank2 nationRank;

    private long unixRegisterDateSeconds = 0;
    private long unixLastOnlineSeconds = 0;

    private Resident( UUID uuid) {
        this.uuid = uuid;
        unixRegisterDateSeconds = System.currentTimeMillis() / 1000L;
        unixLastOnlineSeconds = System.currentTimeMillis() / 1000L;
    }

    private Resident ( User player, Town town, TownRank2 townRank, NationRank2 nationRank, long unixRegisterDateSeconds, long unixLastOnlineSeconds) {
        this.town = town;
        this.uuid = player.getUniqueId();
        this.townRank = townRank;
        this.nationRank = nationRank;
        this.unixRegisterDateSeconds = unixRegisterDateSeconds;
        this.unixLastOnlineSeconds = unixLastOnlineSeconds;
        ResidentManager.getInstance().add(uuid, this);
        ResidentManager.getInstance().saveOne(this);
    }

    public static Resident create ( Player player, Town town, TownRank2 townRank, NationRank2 nationRank, long unixRegisterDateSeconds, long unixLastOnlineSeconds ) {
        return new Resident( player, town, townRank, nationRank, unixRegisterDateSeconds, unixLastOnlineSeconds );
    }

    public static Resident create ( User player ) {
        return new Resident( player, null, null, null, System.currentTimeMillis() / 1000L, System.currentTimeMillis() / 1000L );
    }

    public String getName() {
        if ( !getUser().isPresent() ) return Settings.NON_PLAYER_CHARACTER_NAME;
        return getUser().get().getName();
    }


    // return user object regardless if player is online or not
    private Optional<? extends User> getUser() {
        return UserUtils.getUser(uuid);
    }

    // returns player object IF player is online
    public Optional<Player> getPlayer() {
        for ( Player player : Sponge.getServer().getOnlinePlayers() ) {
            if ( player.getUniqueId().equals(uuid) ) return Optional.of(player);
        }
        return Optional.empty();
    }

    public Optional<Town> getTown() {
        if ( town != null ) return Optional.of(town);
        else return Optional.empty();
    }

    public Optional<Nation> getNation() {
        return NationManager.getInstance().getByResident(this);
    }

    public void setTown( Town town, TownRank2 rank ) {
        this.town = town;
        this.townRank = rank;
    }

    public TownRank2 getTownRank() {
        return townRank;
    }

    public NationRank2 getNationRank() {
        return nationRank;
    }

    public Resident setTownRank(TownRank2 townRank) {
        this.townRank = townRank;
        return this;
    }

    public long getRegisteredSeconds() { return unixRegisterDateSeconds; }

    public long getLastOnlineSeconds() { return unixLastOnlineSeconds; }

    public Date getRegisterDate() {
        return Date.from( Instant.ofEpochSecond(unixRegisterDateSeconds) );
    }

    public String getFormattedRegisterDate() {
        return new SimpleDateFormat("dd-MM-yyyy @ HH:mm").format(getRegisterDate());
    }

    public String getFormattedLastOnlineDate() {
        return new SimpleDateFormat("dd-MM-yyyy @ HH:mm").format(getLastOnlineDate());
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

    @Override
    public Text getFormattedInfo() {

        TextFormat format = TextFormat.NONE;
        String townName = "None";
        Text townLore = Text.EMPTY;
        Text nation = Text.of("None");

        if ( getTown().isPresent() ) {

            townName = getTown().get().getName();
            format = format.color( getTown().get().getColor() );
            format = format.style( TextStyles.BOLD );

            townLore = getTown().get().getFormattedInfo();
            if ( town.getParent().isPresent() ) {
                nation = Text.of ( town.getParent().get().getColor(), town.getParent().get().getName() );
                nation = nation.toBuilder().onHover(TextActions.showText( town.getParent().get().getFormattedInfo() ) ).build();
            }
        }

        return Text.builder()
                .append(Text.of(Settings.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(Settings.TEXT_COLOR, TextStyles.BOLD, getName(), TextStyles.RESET ) )
                .append(Text.of(TextColors.RESET, Settings.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET))
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Registered: ", TextStyles.RESET, Settings.TEXT_COLOR, getFormattedRegisterDate(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Last Online: ", TextStyles.RESET, Settings.TEXT_COLOR, getPlayer().isPresent() ? Text.of( TextColors.GREEN, TextStyles.BOLD, "Now" ) : Text.of( TextColors.RED, getFormattedLastOnlineDate() ), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Bank: ", TextStyles.RESET, Settings.TEXT_COLOR, getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Town: ", TextStyles.RESET, Text.of( format, townName, TextStyles.RESET).toBuilder().onHover(TextActions.showText(townLore)).build(), Settings.DECORATION_COLOR, " ( ", Settings.TEXT_COLOR, nation, Settings.DECORATION_COLOR, " )\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Rank: ", TextStyles.RESET, Settings.TEXT_COLOR, townRank == null ? "None" : townRank.getName() ) )
                .build();
    }

    //public boolean can ( TownsAction action ) {
    //    return !action.isNone() && ( Settings.TOWN_RANK_PERMISSIONS.get(townRank).contains(action) || Settings.NATION_RANK_PERMISSIONS.get(nationRank).contains(action) );
    //}
//
    //public boolean can (TownRank.Action action) {
    //    return !action.equals(TownRank.Action.NONE) && Settings.TOWN_RANK_PERMISSIONS.get(townRank).contains(action);
    //}
//
    //public boolean can (NationRank.Action action) {
    //    return !action.equals(NationRank.Action.NONE) && Settings.NATION_RANK_PERMISSIONS.get(nationRank).contains(action);
    //}

    public Resident leaveTown() {
        Optional<Player> player = getPlayer();
        player.ifPresent(player1 -> Question.poll(player1, Text.of("Would you like to leave your current town?"), Question.Type.YES_NO,
                // yes
                commandSource -> {
                    if ( getTown().isPresent() ) {
                        this.setTown(null, null);
                        getTown().get().warnResidents(Text.of( this.getName() + " has left the town."));
                    }
                }));
        return this;
    }

    public Optional<UniqueAccount> getBank() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();
        return economy.flatMap(economyService -> economyService.getOrCreateAccount(uuid));
    }

    public void setNationRank(NationRank2 nationRank) {
        this.nationRank = nationRank;
    }

    public void setRegisteredTimestamp(long registeredTimestamp) {
        this.unixRegisterDateSeconds = registeredTimestamp;
    }

    public Text getFormattedBank() {
        Optional<EconomyService> economy = AtherysTowns.getInstance().getEconomyService();

        if ( economy.isPresent() ) {
            Optional<UniqueAccount> bankOpt = this.getBank();

            if ( bankOpt.isPresent() ) {
                UniqueAccount bank = bankOpt.get();
                Text.Builder builder = Text.builder();
                builder.append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET));
                Text.Builder hoverText = Text.builder();
                Iterator<Map.Entry<Currency, BigDecimal>> iter = bank.getBalances().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Currency, BigDecimal> entry = iter.next();
                    hoverText.append(Text.of(Settings.SECONDARY_COLOR, entry.getValue(), Settings.DECORATION_COLOR, " ", entry.getKey().getDisplayName()));
                    if (iter.hasNext()) {
                        hoverText.append(Text.of("\n"));
                    }
                }
                return builder.onHover(TextActions.showText(hoverText.build())).build();
            }

        }

        return Text.of("None");
    }

    //@Override
    //public Document toBSON() {
    //    Document serialized = new Document();
    //    serialized.append("_id", this.getUUID());
    //    serialized.append("town", town.getUUID());
    //    serialized.append("last_online", this.getLastOnlineSeconds());
    //    serialized.append("registered", this.getRegisteredSeconds());
    //    serialized.append("town_rank", this.townRank.id());
    //    serialized.append("nation_rank", this.nationRank.id());
    //    return serialized;
    //}
}
