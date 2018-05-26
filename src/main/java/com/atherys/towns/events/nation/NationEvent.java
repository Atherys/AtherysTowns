package com.atherys.towns.events.nation;

import com.atherys.towns.api.nation.INation;
import org.spongepowered.api.event.Event;

public abstract class NationEvent implements Event {

    private INation nation;

    protected NationEvent(INation nation) {
        this.nation = nation;
    }

    public INation getNation() {
        return nation;
    }

}
