package com.atherys.towns.api;

import org.spongepowered.api.text.format.TextColor;

public abstract class Meta {

    private String name;
    private String description;
    private TextColor color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

}
