package com.atherys.towns.api;

import org.spongepowered.api.text.format.TextColor;

public interface MetaHolder {

    <T extends Meta> T getMeta();

    default String getName() {
        return getMeta().getName();
    }

    default void setName(String name) {
        getMeta().setName(name);
    }

    default String getDescription() {
        return getMeta().getDescription();
    }

    default void setDescription(String description) {
        getMeta().setDescription(description);
    }

    default TextColor getColor() {
        return getMeta().getColor();
    }

    default void setColor(TextColor color) {
        getMeta().setColor(color);
    }

}
