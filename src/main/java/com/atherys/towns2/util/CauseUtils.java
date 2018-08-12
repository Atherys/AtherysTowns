package com.atherys.towns2.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;

public final class CauseUtils {

    public static Cause of(Object... objects) {
        Cause.Builder builder = Cause.builder();
        for ( Object object : objects ) builder.append(object);
        return builder.build(Sponge.getCauseStackManager().getCurrentContext());
    }

}
