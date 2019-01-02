package com.atherys.towns.model.permission;

import com.atherys.towns.model.Plot;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class PlotPermissionContext extends AbstractPermissionContext<Plot> {

    @OneToOne
    private Plot holder;

    @Override
    public Plot getHolder() {
        return holder;
    }
}
