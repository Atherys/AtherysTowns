package com.atherys.towns2.nation;

import com.atherys.towns2.base.LocationContainer;
import com.atherys.towns2.base.ResidentContainer;
import com.atherys.towns2.resident.Resident;
import com.atherys.towns2.town.Town;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Nation implements LocationContainer<World>, ResidentContainer {

    private UUID uuid;

    private World world;
    private List<Town> towns;

    @Override
    public World getExtent() {
        return world;
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

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public Set<Resident> getResidents() {
        return null;
    }
}
