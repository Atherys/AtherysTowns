package com.atherys.towns.managers;

import com.atherys.towns.base.BaseAreaObject;
import com.atherys.towns.db.DatabaseManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AreaObjectManager<T extends BaseAreaObject> extends DatabaseManager<T>{

    protected List<T> list;

    AreaObjectManager() {
        list = new LinkedList<>();
    }

    public Optional<T> getByName(String name) {
        for ( T n : list ) {
            if ( n.getName().contains(name) ) return Optional.of(n);
        }
        return Optional.empty();
    }

    public Optional<T> getByUUID ( UUID uuid ) {
        for ( T t : list ) {
            if (t.getUUID().equals(uuid)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Optional<T> getByLocation ( Location<World> loc ) {
        for ( T t : list ) {
            if ( t.contains(loc) ) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public List<T> getAll() { return list; }

    public void add ( T ao ) {
        if ( list.contains(ao) ) return;
        list.add(ao);
    }

    public void remove ( T ao ) {
        list.remove(ao);
    }

}
