package com.atherys.towns.persistence.converter;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.persistence.AttributeConverter;

public class TextColorConverter implements AttributeConverter<TextColor, String> {
    @Override
    public String convertToDatabaseColumn(TextColor attribute) {
        return attribute.getId();
    }

    @Override
    public TextColor convertToEntityAttribute(String dbData) {
        return Sponge.getGame().getRegistry().getType(TextColor.class, dbData).orElse(TextColors.RESET);
    }
}
