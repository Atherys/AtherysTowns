package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.api.permission.world.WorldPermission;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;

public class TownsPermissionContextConverter implements AttributeConverter<TownsPermissionContext, String> {
    @Override
    public String convertToDatabaseColumn(TownsPermissionContext attribute) {
        return attribute.getId();
    }

    @Override
    public TownsPermissionContext convertToEntityAttribute(String dbData) {
        return Sponge.getRegistry().getType(TownsPermissionContext.class, dbData).get();
    }
}
