package com.atherys.towns.api;

import org.spongepowered.api.util.Identifiable;

public interface Context<T extends Context> extends Identifiable {

    boolean hasParent();

    T getParent();

}
