package com.atherys.towns.api.resident;

import com.atherys.core.database.api.DBObject;
import com.atherys.core.views.Viewable;
import com.atherys.towns.api.BankHolder;
import com.atherys.towns.api.town.ITown;
import com.atherys.towns.views.ResidentView;
import java.util.Date;
import java.util.Optional;
import org.spongepowered.api.entity.living.player.User;

/**
 * A resident is a wrapper around a player object for the Towns plugin
 */
public interface IResident extends DBObject, BankHolder, Viewable<ResidentView> {

    Optional<? extends User> asUser();

    Optional<ITown> getTown();

    void setTown(ITown town);

    Date getRegistrationDate();

    Date getLastOnlineDate();

    boolean isOnline();
}
