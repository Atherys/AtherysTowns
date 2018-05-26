package com.atherys.towns.resident;

import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.town.Town;
import java.util.Date;
import java.util.UUID;

public class ResidentBuilder {

    private Resident res;

    ResidentBuilder(UUID uuid) {
        res = new Resident(uuid);
    }

    public ResidentBuilder town(Town town) {
        if (town != null) {
            res.setTown(town);
        }
        return this;
    }

    public ResidentBuilder registerTimestamp(Date time) {
        res.setRegistrationDate(time);
        return this;
    }

    public ResidentBuilder updateLastOnline() {
        res.setLastOnlineDate(new Date());
        return this;
    }

    public Resident build() {
        ResidentManager.getInstance().save(res);
        return res;
    }
}
