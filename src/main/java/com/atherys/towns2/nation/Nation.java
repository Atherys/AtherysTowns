package com.atherys.towns2.nation;

import com.atherys.towns2.base.LocationContainer;
import com.atherys.towns2.town.Town;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;

public class Nation implements LocationContainer<World>, Identifiable {

    private List<Town> towns;

    @Override
    public World getExtent() {
        return null;
    }

    @Override
    public boolean contains(Location<World> location) {
        return false;
    }

    @Override
    public int getArea() {
        return 0;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }
}
