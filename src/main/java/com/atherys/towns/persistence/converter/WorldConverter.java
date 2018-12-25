package com.atherys.towns.persistence.converter;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import javax.persistence.AttributeConverter;
import java.util.UUID;

public class WorldConverter implements AttributeConverter<World, UUID> {
    @Override
    public UUID convertToDatabaseColumn(World attribute) {
        return attribute.getUniqueId();
    }

    @Override
    public World convertToEntityAttribute(UUID dbData) {
        return Sponge.getServer().getWorld(dbData).get();
    }
}
