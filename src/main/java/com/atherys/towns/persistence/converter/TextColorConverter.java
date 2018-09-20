package com.atherys.towns.persistence.converter;

import com.atherys.core.utils.MorphiaTypeConverter;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.mongodb.morphia.mapping.MappedField;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class TextColorConverter extends MorphiaTypeConverter<TextColor> {
    @Override
    protected TextColor deserialize(Class<?> aClass, Object o, MappedField mappedField) {
        return Sponge.getRegistry().getType(TextColor.class, ((BasicDBObject) o).getString("id") ).get();
    }

    @Override
    protected Object serialize(TextColor textColor, MappedField mappedField) {
        return new Document("id", textColor.getId());
    }

    @Override
    protected boolean isSupported(Class<?> aClass) {
        return TextColors.class.isAssignableFrom(aClass);
    }
}
