package com.atherys.towns2;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import static com.atherys.towns2.AtherysTowns.*;

@Plugin(id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "griefprevention")
        })
public class AtherysTowns {

    public final static String ID = "atherystowns";
    public final static String NAME = "A'therys Towns";
    public final static String DESCRIPTION = "A custom plugin responsible for agile land management. Created for the A'therys Horizons server.";
    public final static String VERSION = "1.0.0a";

}
