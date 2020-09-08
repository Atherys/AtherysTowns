package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.WorldPermissionRegistryModule;
import com.atherys.towns.api.permission.world.WorldPermission;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionConverter implements AttributeConverter<WorldPermission, String> {

    @Override
    public String convertToDatabaseColumn(WorldPermission attribute) {
        return attribute.getId();
    }

    @Override
    public WorldPermission convertToEntityAttribute(String dbData) {
        return Sponge.getRegistry().getType(WorldPermission.class, dbData).get();
    }
}
