package com.atherys.towns.base;

import org.spongepowered.api.text.Text;

import java.util.UUID;

public interface TownsObject {

    UUID getUUID();

    void setName(String name);

    String getName();

    Text getFormattedInfo();

}
