package com.atherys.towns.events.town;

import com.atherys.towns.api.town.ITown;
import org.spongepowered.api.event.Event;

public abstract class TownEvent implements Event {

    private ITown town;

    protected TownEvent(ITown town) {
        this.town = town;
    }

    public ITown getTown() {
        return town;
    }

}
