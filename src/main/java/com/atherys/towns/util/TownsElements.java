package com.atherys.towns.util;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

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
                () -> AtherysTowns.getInstance().getNationService().getNations().keySet(),
                string -> AtherysTowns.getInstance().getNationService().getNationFromId(string).orElse(null)
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
                AtherysTowns.getInstance().getConfig().NATION_ROLES.keySet().stream()
                        .collect(Collectors.toMap(s -> s, s -> s))
        );
    }
}
