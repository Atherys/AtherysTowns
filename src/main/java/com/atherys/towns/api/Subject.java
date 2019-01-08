package com.atherys.towns.api;

import org.spongepowered.api.util.Identifiable;

public interface Subject<T extends Subject> extends Identifiable {

    boolean hasParent();

    T getParent();

}
