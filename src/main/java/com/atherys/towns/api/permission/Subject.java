package com.atherys.towns.api.permission;

import com.atherys.core.db.Identifiable;

import java.io.Serializable;

public interface Subject<T extends Subject, ID extends Serializable> extends Identifiable<ID> {

    boolean hasParent();

    T getParent();

}
