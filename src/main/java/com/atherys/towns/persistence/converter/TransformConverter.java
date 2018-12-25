package com.atherys.towns.persistence.converter;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import javax.persistence.AttributeConverter;

public class TransformConverter implements AttributeConverter<Transform<World>, String> {

    @Override
    public String convertToDatabaseColumn(Transform<World> attribute) {
        return "";
    }

    @Override
    public Transform<World> convertToEntityAttribute(String dbData) {
        return null;
    }
}
