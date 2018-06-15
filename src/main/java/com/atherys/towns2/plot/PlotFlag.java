package com.atherys.towns2.plot;

import me.ryanhamshire.griefprevention.api.claim.ClaimFlag;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(PlotFlags.class)
public class PlotFlag implements CatalogType {

    private String id;
    private String name;

    private ClaimFlag claimFlag;

    protected PlotFlag(String id, String name, ClaimFlag claimFlag) {
        this.id = id;
        this.name = name;
        this.claimFlag = claimFlag;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public ClaimFlag getClaimFlag() {
        return claimFlag;
    }
}
