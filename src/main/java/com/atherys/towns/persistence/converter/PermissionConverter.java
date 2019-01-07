package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.Permission;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;

public class PermissionConverter implements AttributeConverter<Permission, String> {
    @Override
    public String convertToDatabaseColumn(Permission attribute) {
        return attribute.getId();
    }

    @Override
    public Permission convertToEntityAttribute(String dbData) {
        return Sponge.getRegistry().getType(Permission.class, dbData).orElse(null);
    }
}
