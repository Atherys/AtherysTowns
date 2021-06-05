package com.atherys.towns.model;

import com.atherys.towns.util.Rectangle;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.util.UUID;

public class BorderInfo implements Rectangle {

    private Vector3i neCorner;
    private Vector3i swCorner;
    private final boolean isCuboid;
    private final ParticleEffect effect;

    public BorderInfo(Vector3i neCorner, Vector3i swCorner, boolean isCuboid, ParticleEffect effect) {
        this.neCorner = neCorner;
        this.swCorner = swCorner;
        this.isCuboid = isCuboid;
        this.effect = effect;
    }

    public Vector3i getNECorner() {
        return this.neCorner;
    }

    public Vector3i getSWCorner() {
        return this.swCorner;
    }

    public ParticleEffect getEffect() {
        return this.effect;
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

    public boolean isCuboid() {
        return isCuboid;
    }
}
