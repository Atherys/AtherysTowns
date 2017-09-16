package com.atherys.towns.base;

import com.atherys.towns.utils.DatabaseUtils;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.UUID;

public interface TownsObject {

    UUID getUUID();

    void setName(String name);

    String getName();

    Text getFormattedInfo();

    Map<? extends DatabaseUtils.AbstractTable, Object> toDatabaseStorable();

}
