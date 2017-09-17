package com.atherys.towns.resident;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.Settings;
import com.atherys.towns.base.TownsObject;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.resident.ranks.NationRank;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.resident.ranks.TownsAction;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.Question;
import com.atherys.towns.utils.UserUtils;
import io.github.flibio.economylite.EconomyLite;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

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

        public Resident.Builder town ( Town town, TownRank rank ) {
            if ( town != null ) town.addResident(res, rank);
            return this;
        }

        public Resident.Builder townRank ( TownRank rank ) {
            res.setTownRank(rank);
            return this;
        }

        public Resident.Builder nationRank ( NationRank rank ) {
            res.setNationRank(rank);
            return this;
        }

        public Resident.Builder registerTimestamp ( int time ) {
            res.setRegisteredTimestamp(time);
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
            AtherysTowns.getInstance().getResidentManager().add(res.uuid(),res);
            return res;
        }
    }

    private Town town;
    private TownRank townRank = TownRank.NONE;
    private NationRank nationRank = NationRank.NONE;

    private long unixRegisterDateSeconds = 0;
    private long unixLastOnlineSeconds = 0;

    private UUID playerUUID;

    private Resident( UUID uuid) {
        this.playerUUID = uuid;
        unixRegisterDateSeconds = System.currentTimeMillis() / 1000L;
        unixLastOnlineSeconds = System.currentTimeMillis() / 1000L;
    }

    private Resident ( User player, Town town, TownRank townRank, NationRank nationRank, long unixRegisterDateSeconds, long unixLastOnlineSeconds) {
        this.town = town;
        this.playerUUID = player.getUniqueId();
        this.townRank = townRank;
        this.nationRank = nationRank;
        this.unixRegisterDateSeconds = unixRegisterDateSeconds;
        this.unixLastOnlineSeconds = unixLastOnlineSeconds;
        AtherysTowns.getInstance().getResidentManager().add(playerUUID, this);
    }

    private Resident ( UUID uuid, Town town, TownRank townRank, NationRank nationRank, long unixRegisterDateSeconds, long unixLastOnlineSeconds) {
        this.town = town;
        this.playerUUID = uuid;
        this.townRank = townRank;
        this.nationRank = nationRank;
        this.unixRegisterDateSeconds = unixRegisterDateSeconds;
        this.unixLastOnlineSeconds = unixLastOnlineSeconds;
        AtherysTowns.getInstance().getResidentManager().add(playerUUID, this);
    }

    public static Resident create ( Player player, Town town, TownRank townRank, NationRank nationRank, long unixRegisterDateSeconds, long unixLastOnlineSeconds ) {
        return new Resident( player, town, townRank, nationRank, unixRegisterDateSeconds, unixLastOnlineSeconds );
    }

    public static Resident create ( User player ) {
        return new Resident( player, null, TownRank.NONE, NationRank.NONE, System.currentTimeMillis() / 1000L, System.currentTimeMillis() / 1000L );
    }

    public static Resident create( UUID uuid ) {
        return new Resident( uuid, null, TownRank.NONE, NationRank.NONE, System.currentTimeMillis() / 1000L, System.currentTimeMillis() / 1000L );
    }

    public UUID uuid() {
        return playerUUID;
    }

    public String getName() {
        if ( !getUser().isPresent() ) return Settings.NON_PLAYER_CHARACTER_NAME;
        return getUser().get().getName();
    }

    public Optional<? extends User> getUser() {
        return UserUtils.getUser(playerUUID);
    }

    public Optional<Player> getPlayer() {
        return Sponge.getServer().getPlayer(playerUUID);
    }

    public Optional<Town> town() {
        if ( town != null ) return Optional.of(town);
        else return Optional.empty();
    }

    public Resident setTown( Town town, TownRank rank ) {
        this.town = town;
        this.townRank = rank;
        return this;
    }

    public TownRank townRank() {
        return townRank;
    }

    public NationRank nationRank() {
        return nationRank;
    }

    public Resident setTownRank(TownRank townRank) {
        this.townRank = townRank;
        return this;
    }

    public long registerSeconds() { return unixRegisterDateSeconds; }

    public long lastOnlineSeconds() { return unixLastOnlineSeconds; }

    public Date registeredOn() {
        return Date.from( Instant.ofEpochSecond(unixRegisterDateSeconds) );
    }

    public String formattedRegisterDate() {
        return new SimpleDateFormat("dd-MM-yyyy @ HH:mm").format(registeredOn());
    }

    public String formattedLastOnlineDate() {
        return new SimpleDateFormat("dd-MM-yyyy @ HH:mm").format(lastOnline());
    }

    public Date lastOnline() {
        return Date.from( Instant.ofEpochSecond(unixLastOnlineSeconds) );
    }

    public void updateLastOnline() {
        this.unixLastOnlineSeconds = System.currentTimeMillis() / 1000L;
    }

    @Override
    public UUID getUUID() {
        return playerUUID;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public Text getFormattedInfo() {

        if ( !getPlayer().isPresent() ) Text.of("Resident is not assigned to player");

        TextFormat format = TextFormat.NONE;
        String townName = "None";
        Text townLore = Text.EMPTY;
        Text nation = Text.of("None");

        if ( town().isPresent() ) {
            townName = town().get().getName();
            format.color( town().get().color() );
            format.style( TextStyles.BOLD      );
            townLore = town().get().getFormattedInfo();
            if ( town.getParent().isPresent() ) {
                nation = Text.of ( town.getParent().get().color(), town.getParent().get().name() );
                nation = nation.toBuilder().onHover(TextActions.showText( town.getParent().get().formatInfo() ) ).build();
            }
        }

        return Text.builder()
                .append(Text.of(Settings.DECORATION_COLOR, ".o0o.______.[ ", TextColors.RESET))
                .append(Text.of(Settings.TEXT_COLOR, TextStyles.BOLD, getName(), TextStyles.RESET ) )
                .append(Text.of(TextColors.RESET, Settings.DECORATION_COLOR, " ].______.o0o.\n", TextColors.RESET))
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Registered: ", TextStyles.RESET, Settings.TEXT_COLOR, formattedRegisterDate(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Last Online: ", TextStyles.RESET, Settings.TEXT_COLOR, formattedLastOnlineDate(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Bank: ", TextStyles.RESET, Settings.TEXT_COLOR, getFormattedBank(), "\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Town: ", TextStyles.RESET, Text.of( format, townName, TextStyles.RESET).toBuilder().onHover(TextActions.showText(townLore)).build(), Settings.DECORATION_COLOR, " ( ", Settings.TEXT_COLOR, nation, Settings.DECORATION_COLOR, " )\n") )
                .append(Text.of(TextColors.RESET, Settings.PRIMARY_COLOR, TextStyles.BOLD, "Rank: ", TextStyles.RESET, Settings.TEXT_COLOR, townRank.formattedName() ) )
                .build();
    }

    public Text formatInfo() { return getFormattedInfo(); }

    public boolean canClaimPlots() {
        return this.can(TownRank.Action.CLAIM_PLOT);
    }

    public boolean can ( TownsAction action ) {
        return !action.isNone() && ( Settings.TOWN_RANK_PERMISSIONS.get(townRank).contains(action) || Settings.NATION_RANK_PERMISSIONS.get(nationRank).contains(action) );
    }

    public boolean can (TownRank.Action action) {
        return !action.equals(TownRank.Action.NONE) && Settings.TOWN_RANK_PERMISSIONS.get(townRank).contains(action);
    }

    public boolean can (NationRank.Action action) {
        return !action.equals(NationRank.Action.NONE) && Settings.NATION_RANK_PERMISSIONS.get(nationRank).contains(action);
    }

    public Resident leaveTown() {
        Optional<Player> player = getPlayer();
        player.ifPresent(player1 -> Question.poll(player1, Text.of("Would you like to leave your current town?"), Question.Type.YES_NO,
                // yes
                commandSource -> {
                    if ( town().isPresent() ) {
                        town().get().warnResidents(Text.of( this.getName() + " has left the town."));
                        town().get().removeResident(this);
                        this.setTown(null, TownRank.NONE);
                    }
                }));
        return this;
    }

    public Optional<UniqueAccount> getBank() {
        if ( AtherysTowns.getInstance().getEconomyPlugin().isPresent() ) {
            return EconomyLite.getEconomyService().getOrCreateAccount(playerUUID);
        }
        return Optional.empty();
    }

    public void setNationRank(NationRank nationRank) {
        this.nationRank = nationRank;
    }

    public Resident setRegisteredTimestamp(long registeredTimestamp) {
        this.unixRegisterDateSeconds = registeredTimestamp;
        return this;
    }

    public Text getFormattedBank() {
        Optional<UniqueAccount> bankOpt = Optional.empty();
        if ( AtherysTowns.getInstance().getEconomyPlugin().isPresent() ) {
            bankOpt = EconomyLite.getEconomyService().getOrCreateAccount(playerUUID);
        }
        if ( bankOpt.isPresent() ) {
            UniqueAccount bank = bankOpt.get();
            Text.Builder builder = Text.builder();
            builder.append(Text.of(TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET));
            Text.Builder hoverText = Text.builder();
            Iterator<Map.Entry<Currency, BigDecimal>> iter = bank.getBalances().entrySet().iterator();
            while ( iter.hasNext() ) {
                Map.Entry<Currency,BigDecimal> entry = iter.next();
                hoverText.append(Text.of(Settings.SECONDARY_COLOR, entry.getValue(), Settings.DECORATION_COLOR, " ", entry.getKey().getDisplayName() ) );
                if ( iter.hasNext() ) {
                    hoverText.append(Text.of("\n"));
                }
            }
            return builder.onHover(TextActions.showText(hoverText.build())).build();
        } else {
            return Text.of("None");
        }
    }

    @Override
    public Map<ResidentManager.Table, Object> toDatabaseStorable() {
        Map<ResidentManager.Table, Object> map = new HashMap<>();

        String town_uuid = "NULL";
        if ( town().isPresent() ) town_uuid = town().get().getUUID().toString();

        map.put(ResidentManager.Table.UUID, getUUID().toString() );
        map.put(ResidentManager.Table.TOWN_UUID, town_uuid);
        map.put(ResidentManager.Table.TOWN_RANK, townRank.id() );
        map.put(ResidentManager.Table.NATION_RANK, nationRank.id() );
        map.put(ResidentManager.Table.REGISTER, registerSeconds() );
        map.put(ResidentManager.Table.LAST_ONLINE, lastOnlineSeconds() );

        return map;
    }
}
