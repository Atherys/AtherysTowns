package com.atherys.towns.api.permission;

import org.spongepowered.api.util.Identifiable;

public interface Subject<T extends Subject> extends Identifiable {

    boolean hasParent();

    T getParent();

}
