package com.atherys.towns.persistence.converter;

import com.atherys.towns.util.Permissions;

import javax.persistence.AttributeConverter;

import static com.atherys.towns.util.Permissions.*;

public class PermissionsConverter implements AttributeConverter<Permissions, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Permissions attribute) {
        Permissions.of(DAMAGE_PLAYERS | DAMAGE_ENTITIES | PLACE_BLOCKS | DESTROY_BLOCKS);
        return Permissions.toInteger(attribute);
    }

    @Override
    public Permissions convertToEntityAttribute(Integer dbData) {
        return Permissions.of(dbData);
    }
}
