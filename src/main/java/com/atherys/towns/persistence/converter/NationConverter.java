package com.atherys.towns.persistence.converter;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.Nation;

import javax.persistence.AttributeConverter;

public class NationConverter implements AttributeConverter<Nation, String> {
    @Override
    public String convertToDatabaseColumn(Nation attribute) {
        return attribute.getId();
    }

    @Override
    public Nation convertToEntityAttribute(String dbData) {
        return AtherysTowns.getInstance().getNationService().getNationFromId(dbData).orElse(null);
    }
}
