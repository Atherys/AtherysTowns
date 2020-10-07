package com.atherys.towns.api.event;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public abstract class TownEvent implements Event {
    private final Cause cause;

    private final Town town;

    protected TownEvent(Town town) {
        this.cause = Cause.builder().append(town).append(AtherysTowns.getInstance()).build(Sponge.getCauseStackManager().getCurrentContext());
        this.town = town;
    }

    public Town getTown() {
        return town;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public static class Created extends TownEvent {
        public Created(Town town) {
            super(town);
        }
    }

    public static class Removed extends TownEvent {
        public Removed(Town town) {
            super(town);
        }
    }

    public static class Renamed extends TownEvent {

        private final String oldName;
        private final String newName;

        public Renamed(Town town, String oldName, String newName) {
            super(town);
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
