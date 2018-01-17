package com.atherys.towns.managers;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.base.BaseAreaObject;
import com.atherys.towns.db.TownsDatabase;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public abstract class AreaObjectManager<T extends BaseAreaObject> extends AbstractMongoDatabaseManager<T> {

    protected AreaObjectManager( String collectionName ) {
        super(AtherysTowns.getInstance().getLogger(), TownsDatabase.getInstance(), collectionName );
    }

    public Optional<T> getByName(String name) {
        for ( T n : super.getCache().values() ) {
            if ( n.getName().contains(name) ) return Optional.of(n);
        }
        return Optional.empty();
    }

    public Optional<T> getByUUID ( UUID uuid ) {
        return Optional.ofNullable( super.getCache().get( uuid ) );
    }

    public Optional<T> getByLocation ( Location<World> loc ) {
        for ( T t : super.getCache().values() ) {
            if ( t.contains(loc) ) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Collection<T> getAll() { return super.getCache().values(); }

    public void add ( T ao ) {
        if ( super.getCache().containsKey( ao.getUUID() ) ) return;
        super.getCache().put( ao.getUUID(), ao );
    }

    public <P extends BaseAreaObject> List<T> getByParent ( P test ) {
        List<T> children = new ArrayList<>();
        for ( T t : super.getCache().values() ) {
            Optional<? extends BaseAreaObject> parent = t.getParent();
            if ( parent.isPresent() ) {
                if ( parent.get().equals(test) ) {
                    children.add(t);
                }
            }
        }
        return children;
    }

}
