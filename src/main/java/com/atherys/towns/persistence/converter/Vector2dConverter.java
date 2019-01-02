package com.atherys.towns.persistence.converter;

import com.flowpowered.math.vector.Vector2d;

import javax.persistence.AttributeConverter;

public class Vector2dConverter implements AttributeConverter<Vector2d, String> {

    private static final String SPLIT_CHARACTERS = "; ";

    @Override
    public String convertToDatabaseColumn(Vector2d attribute) {
        return attribute.getX() + SPLIT_CHARACTERS + attribute.getY();
    }

    @Override
    public Vector2d convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(SPLIT_CHARACTERS);
        return new Vector2d(Double.valueOf(data[0]), Double.valueOf(data[1]));
    }
}
