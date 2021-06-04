package com.atherys.towns.model;

import com.atherys.towns.util.Rectangle;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.util.UUID;

public class BorderInfo implements Rectangle {

    private ParticleEffect effect;
    private Vector3i neCorner;
    private Vector3i swCorner;
    private UUID playerUUID;

    public BorderInfo(ParticleEffect effect, UUID playerUUID, Vector3i neCorner, Vector3i swCorner) {
        this.effect = effect;
        this.playerUUID = playerUUID;
        this.neCorner = neCorner;
        this.swCorner = swCorner;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Vector3i getNECorner() {
        return this.neCorner;
    }

    public void setNECorner(Vector3i neCorner) {
        this.neCorner = neCorner;
    }

    public Vector3i getSWCorner() {
        return this.swCorner;
    }

    public void setSWCorner(Vector3i swCorner) {
        this.swCorner = swCorner;
    }

    public ParticleEffect getEffect() {
        return this.effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    @Override
    public Vector2i getTopLeftCorner() {
        return swCorner.toVector2(true);
    }

    @Override
    public Vector2i getBottomRightCorner() {
        return neCorner.toVector2(true);
    }

    @Override
    public void setTopLeftCorner(Vector3i point) {
        swCorner = point;
    }

    @Override
    public void setBottomRightCorner(Vector3i point) {
        neCorner = point;
    }
}
