package com.atherys.towns.model.permission;

import com.atherys.towns.model.Town;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class TownPermissionContext extends AbstractPermissionContext<Town> {

    @OneToOne
    private Town holder;

    @Override
    public Town getHolder() {
        return holder;
    }
}
