package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.TownsPermissionContext;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;

public class TownPermissionContextConverter implements AttributeConverter<TownsPermissionContext, String> {
    @Override
    public TownsPermissionContext convertToEntityAttribute(String id) {
        return Sponge.getRegistry().getType(TownsPermissionContext.class, id).get();
    }

    @Override
    public String convertToDatabaseColumn(TownsPermissionContext townsPermissionContext) {
        return townsPermissionContext.getId();
    }
}
