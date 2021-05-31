package com.atherys.towns.persistence.converter;

import com.flowpowered.math.vector.Vector3i;

import javax.persistence.AttributeConverter;

public class Vector3iConverter implements AttributeConverter<Vector3i, String> {

    private static final String SPLIT_CHARACTERS = "; ";

    @Override
    public String convertToDatabaseColumn(Vector3i attribute) {
        return attribute.getX() + SPLIT_CHARACTERS + attribute.getY() + SPLIT_CHARACTERS + attribute.getZ();
    }

    @Override
    public Vector3i convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(SPLIT_CHARACTERS);
        return new Vector3i(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    }
}
