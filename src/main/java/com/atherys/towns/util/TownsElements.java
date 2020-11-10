package com.atherys.towns.util;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.permission.TownsPermissionContext;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.PlotService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class TownsElements {
    public static CommandElement town() {
        return GenericArguments.choices(
                Text.of("town"),
                () -> AtherysTowns.getInstance().getTownRepository().getAll().stream().map(Town::getName).collect(Collectors.toList()),
                string -> AtherysTowns.getInstance().getTownService().getTownFromName(string).orElse(null)
        );
    }

    public static CommandElement nation() {
        return GenericArguments.choices(
                Text.of("nation"),
                () -> AtherysTowns.getInstance().getNationRepository().getAll().stream().map(Nation::getName).collect(Collectors.toList()),
                string -> AtherysTowns.getInstance().getNationService().getNationFromName(string).orElse(null)
        );
    }

    public static CommandElement townPermissionContext() {
        Map<String,TownsPermissionContext> contexts = new HashMap<>();
        Sponge.getRegistry().getAllOf(TownsPermissionContext.class).forEach(c -> {
            contexts.put(c.getCommandElementName(), c);
        });

        return GenericArguments.choices(
                Text.of("type"),
                contexts
        );
    }

    public static CommandElement townRole() {
        return GenericArguments.choices(
                Text.of("role"),
                AtherysTowns.getInstance().getConfig().TOWN.ROLES.keySet().stream()
                        .collect(Collectors.toMap(s -> s, s -> s))
        );
    }

    public static CommandElement nationRole() {
        return GenericArguments.choices(
                Text.of("role"),
                AtherysTowns.getInstance().getConfig().NATION.ROLES.keySet().stream()
                        .collect(Collectors.toMap(s -> s, s -> s))
        );
    }
}
