package com.atherys.towns.model.entity;

import javax.persistence.*;

@Entity
@Table(schema = "atherystowns", name = "NationPlot")
public class NationPlot extends Plot {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "nation_id")
    private Nation nation;

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    @Override
    public boolean isCuboid() {
        return false;
    }
}
