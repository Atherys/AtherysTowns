package com.atherys.towns2;

import com.atherys.core.utils.PluginConfig;

import java.io.IOException;

public final class TownsConfig extends PluginConfig {

    public static final String DEFAULT_PLOT_NAME = "Plot";

    protected TownsConfig(String directory, String filename) throws IOException {
        super(directory, filename);
    }
}

