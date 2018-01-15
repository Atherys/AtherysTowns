package com.atherys.towns.managers;

import com.atherys.towns.base.BaseAreaObject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public abstract class AreaObjectManager<T extends BaseAreaObject> extends DatabaseManager<T> {

    private Map<UUID,T> objects = new HashMap<>();

    public Optional<T> getByName(String name) {
        for ( T n : objects.values() ) {
            if ( n.getName().contains(name) ) return Optional.of(n);
        }
        return Optional.empty();
    }

    public Optional<T> getByUUID ( UUID uuid ) {
        return Optional.ofNullable( objects.get( uuid ) );
    }

    public Optional<T> getByLocation ( Location<World> loc ) {
        for ( T t : objects.values() ) {
            if ( t.contains(loc) ) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Collection<T> getAll() { return objects.values(); }

    public void add ( T ao ) {
        if ( objects.containsKey( ao.getUUID() ) ) return;
        objects.put( ao.getUUID(), ao );
    }

    public <P extends BaseAreaObject> List<T> getByParent ( P test ) {
        List<T> children = new ArrayList<>();
        for ( T t : objects.values() ) {
            Optional<? extends BaseAreaObject> parent = t.getParent();
            if ( parent.isPresent() ) {
                if ( parent.get().equals(test) ) {
                    children.add(t);
                }
            }
        }
        return children;
    }

    public void remove ( T ao ) {
        objects.remove( ao.getUUID() );
        removeOne(ao);
    }

}
