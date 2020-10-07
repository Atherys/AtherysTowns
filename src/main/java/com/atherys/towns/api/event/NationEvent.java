package com.atherys.towns.api.event;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public abstract class NationEvent implements Event {

    private final Cause cause;

    private final Nation nation;

    protected NationEvent(Nation nation) {
        this.cause = Cause.builder().append(nation).append(AtherysTowns.getInstance()).build(Sponge.getCauseStackManager().getCurrentContext());
        this.nation = nation;
    }

    public Nation getNation() {
        return nation;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public static class Created extends NationEvent {
        public Created(Nation nation) {
            super(nation);
        }
    }

    public static class Removed extends NationEvent {
        public Removed(Nation nation) {
            super(nation);
        }
    }

    public static class Renamed extends NationEvent {
        private final String oldName;
        private final String newName;

        public Renamed(Nation nation, String oldName, String newName) {
            super(nation);
            this.oldName = oldName;
            this.newName = newName;
        }

        public String getOldName() {
            return oldName;
        }

        public String getNewName() {
            return newName;
        }
    }
}
