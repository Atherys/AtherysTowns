package com.atherys.towns.managers;

import com.atherys.core.database.mongo.AbstractMongoDatabaseManager;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.base.AreaObject;
import com.atherys.towns.db.TownsDatabase;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

/**
 * An abstract implementation of {@link AbstractMongoDatabaseManager} with {@link AreaObject}s in mind. This class utilizes caching.
 *
 * @param <T> The AreaObject this manager will be managing
 */
public abstract class AreaObjectManager<T extends AreaObject> extends AbstractMongoDatabaseManager<T> {

    protected AreaObjectManager ( String collectionName ) {
        super( AtherysTowns.getInstance().getLogger(), TownsDatabase.getInstance(), collectionName );
    }


    /**
     * Retrieve all {@link AreaObject}s from the database whose name equals ( See: {@link String#equals(Object)} ) the given String.
     *
     * @param name The name to check for
     * @return An optional containing a list of results.
     */
    public Optional<List<T>> getByName ( String name ) {
        List<T> list = new ArrayList<>();
        for ( T n : super.getCache().values() ) {
            if ( n.getName().equals( name ) ) list.add( n );
        }
        if ( list.isEmpty() ) return Optional.empty();
        return Optional.of( list );
    }

    /**
     * Uses {@link #getByName(String)} to retrieve a list of results and instead returns the first result within the list.
     * If the returned list is empty, this will return an empty optional.
     *
     * @param name The name to check for
     * @return The result
     */
    public Optional<T> getFirstByName ( String name ) {
        List<T> results = getByName( name ).orElse( new ArrayList<>() );
        if ( results.isEmpty() ) return Optional.empty();
        else return Optional.of( results.get( 0 ) );
    }

    /**
     * Retrieve an {@link AreaObject} using it's UUID
     *
     * @param uuid The UUID to be searched for
     * @return An optional containing the result. If no object could be found given these parameters, this will be empty.
     */
    public Optional<T> getByUUID ( UUID uuid ) {
        return Optional.ofNullable( super.getCache().get( uuid ) );
    }

    /**
     * Retrieve an {@link AreaObject} based on whether or not the provided location is contained within it. AreaObjects must not be permitted to overlap.
     *
     * @param loc The location to be searched for
     * @return An optional containing the result. If no object could be found given these parameters, this will be empty.
     */
    public Optional<T> getByLocation ( Location<World> loc ) {
        for ( T t : super.getCache().values() ) {
            if ( t.contains( loc ) ) {
                return Optional.of( t );
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the cached values.
     *
     * @return The cached values
     */
    public Collection<T> getAll () {
        return super.getCache().values();
    }

    /**
     * Add a new {@link AreaObject} to the cache. This will be saved to the database at an appropriate time.
     *
     * @param ao The AreaObject to be cached.
     */
    public void add ( T ao ) {
        if ( super.getCache().containsKey( ao.getUUID() ) ) return;
        super.getCache().put( ao.getUUID(), ao );
    }

    /**
     * Retrieve a list of all {@link AreaObject}s which have the provided object as their parent.
     *
     * @param test The parent to be searched for.
     * @param <P>  The type of the parent ( See: {@link AreaObject} documentation for reference )
     * @return The list of children the provided object is a parent of.
     */
    public <P extends AreaObject> List<T> getByParent ( P test ) {
        List<T> children = new ArrayList<>();
        for ( T t : super.getCache().values() ) {
            Optional<? extends AreaObject> parent = t.getParent();
            if ( parent.isPresent() ) {
                if ( parent.get().equals( test ) ) {
                    children.add( t );
                }
            }
        }
        return children;
    }

}
