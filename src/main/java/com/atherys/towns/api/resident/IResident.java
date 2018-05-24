package com.atherys.towns.api.resident;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.views.Viewable;
import com.atherys.towns.api.BankHolder;
import com.atherys.towns.api.plot.flag.Flag;
import com.atherys.towns.api.plot.flag.FlagHolder;
import com.atherys.towns.api.resident.permission.Rank;
import com.atherys.towns.api.resident.permission.RankHolder;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.ResidentView;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Tristate;

import java.util.Date;
import java.util.Optional;

/**
 * A resident is a wrapper around a player object for the Towns plugin
 */
public interface IResident extends DBObject, BankHolder, RankHolder, Viewable<ResidentView> {

    Optional<ITown> getTown();

    void setTown(ITown town);

    Optional<? extends User> asUser();

    Optional<? extends Rank> getTownRank();

    <T extends Rank> void setTownRank(T rank);

    Optional<? extends Rank> getNationRank();

    <T extends Rank> void setNationRank(T rank);

    Date getRegistrationDate();

    Date getLastOnlineDate();

    boolean isOnline();

    <T extends FlagHolder> void modify(Flag flag, T holder, Tristate tristate);
}
