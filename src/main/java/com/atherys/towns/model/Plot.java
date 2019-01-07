package com.atherys.towns.model;

import com.atherys.core.db.SpongeIdentifiable;
import com.atherys.towns.api.Context;
import com.atherys.towns.persistence.converter.TextConverter;
import com.atherys.towns.persistence.converter.Vector2dConverter;
import com.flowpowered.math.vector.Vector2d;
import org.hibernate.annotations.GenericGenerator;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
public class Plot implements SpongeIdentifiable, Context<Town> {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "town_id")
    private Town town;

    @Convert(converter = TextConverter.class)
    private Text name;

    @Convert(converter = Vector2dConverter.class)
    private Vector2d nwCorner;

    @Convert(converter = Vector2dConverter.class)
    private Vector2d seCorner;

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Vector2d getNorthWestCorner() {
        return nwCorner;
    }

    public void setNorthWestCorner(Vector2d nwCorner) {
        this.nwCorner = nwCorner;
    }

    public Vector2d getSouthEastCorner() {
        return seCorner;
    }

    public void setSouthWestCorner(Vector2d seCorner) {
        this.seCorner = seCorner;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Town getParent() {
        return town;
    }
}
