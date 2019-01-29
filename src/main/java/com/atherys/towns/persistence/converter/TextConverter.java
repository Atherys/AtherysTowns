package com.atherys.towns.persistence.converter;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.persistence.AttributeConverter;

public class TextConverter implements AttributeConverter<Text, String> {
    @Override
    public String convertToDatabaseColumn(Text attribute) {
        return TextSerializers.FORMATTING_CODE.serialize(attribute);
    }

    @Override
    public Text convertToEntityAttribute(String dbData) {
        return TextSerializers.FORMATTING_CODE.deserialize(dbData);
    }
}
