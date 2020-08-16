package com.atherys.towns.persistence.converter;

import com.atherys.towns.api.permission.Permission;
import org.spongepowered.api.Sponge;

import javax.persistence.AttributeConverter;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionConverter implements AttributeConverter<Set<Permission>, Set<String>> {

    @Override
    public Set<String> convertToDatabaseColumn(Set<Permission> attributes) {
        return attributes.stream().map(Permission::getName).collect(Collectors.toSet());
    }

    @Override
    public Set<Permission> convertToEntityAttribute(Set<String> dbData) {
        return dbData.stream().map(s -> Sponge.getRegistry().getType(Permission.class, s).orElse(null)).collect(Collectors.toSet());
    }
}
