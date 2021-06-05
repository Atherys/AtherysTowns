package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.world.WorldPermission;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;

public class PermissionConverter implements AttributeConverter<WorldPermission, String> {

    @Override
    public String convertToDatabaseColumn(WorldPermission attribute) {
        return attribute.getId();
    }

    @Override
    public WorldPermission convertToEntityAttribute(String dbData) {
        return Sponge.getRegistry().getType(WorldPermission.class, dbData).get();
    }
}
