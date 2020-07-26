package com.atherys.towns.util;

import com.flowpowered.math.vector.Vector2i;

public interface Rectangle {
    public Vector2i getTopLeftCorner();
    public Vector2i getBottomRightCorner();

    public default int maxX() {
        return getBottomRightCorner().getX();
    }

    public default int minX() {
        return getTopLeftCorner().getX();
    }

    public default int maxY() {
        return getTopLeftCorner().getY();
    }

    public default int minY() {
        return getBottomRightCorner().getY();
    }
}
