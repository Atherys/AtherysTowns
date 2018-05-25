package com.atherys.towns.town;

import com.atherys.towns.api.Meta;

public class TownMeta extends Meta {

    private String motd;

    public void setMOTD(String motd) {
         this.motd = motd;
    }

    public String getMOTD() {
        return motd;
    }

}
