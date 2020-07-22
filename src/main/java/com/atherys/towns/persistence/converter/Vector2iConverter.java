package com.atherys.towns.persistence.converter;

import com.flowpowered.math.vector.Vector2i;

import javax.persistence.AttributeConverter;

public class Vector2iConverter implements AttributeConverter<Vector2i, String> {


    private static final String SPLIT_CHARACTERS = "; ";

    @Override
    public String convertToDatabaseColumn(Vector2i attribute) {
        return attribute.getX() + SPLIT_CHARACTERS + attribute.getY();
    }

    @Override
    public Vector2i convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(SPLIT_CHARACTERS);
        return new Vector2i(Integer.valueOf(data[0]), Double.valueOf(data[1]));
    }
}
