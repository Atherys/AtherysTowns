package com.atherys.towns.persistence.converter;

import com.atherys.core.utils.MorphiaTypeConverter;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.mongodb.morphia.mapping.MappedField;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

public class WorldTypeConverter extends MorphiaTypeConverter<World> {

    private static final String WORLD_NAME_FIELD = "world-name";

    @Override
    protected World deserialize(Class<?> aClass, Object o, MappedField mappedField) {
        BasicDBObject object = (BasicDBObject) o;
        String worldName = object.getString(WORLD_NAME_FIELD, "world");
        return Sponge.getServer().getWorld(worldName).get();
    }

    @Override
    protected Object serialize(World world, MappedField mappedField) {
        return new Document(WORLD_NAME_FIELD, world.getName());
    }

    @Override
    protected boolean isSupported(Class<?> aClass) {
        return World.class.isAssignableFrom(aClass);
    }
}
