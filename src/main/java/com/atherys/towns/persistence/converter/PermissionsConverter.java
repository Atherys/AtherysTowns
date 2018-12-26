package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.Permissions;

import javax.persistence.AttributeConverter;

public class PermissionsConverter implements AttributeConverter<Permissions, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Permissions attribute) {
        return Permissions.toInteger(attribute);
    }

    @Override
    public Permissions convertToEntityAttribute(Integer dbData) {
        return Permissions.of(dbData);
    }
}
