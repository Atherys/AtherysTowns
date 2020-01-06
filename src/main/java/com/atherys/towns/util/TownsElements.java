package com.atherys.towns.util;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Town;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.stream.Collectors;

public final class TownsElements {
    public static CommandElement town() {
        return GenericArguments.choices(
                Text.of("town"),
                () -> AtherysTowns.getInstance().getTownRepository().getAll().stream().map(Town::getName).collect(Collectors.toList()),
                string -> {
                    if (string.isEmpty()) return null;
                    return AtherysTowns.getInstance().getTownService().getTownFromName(string).get();
                }
        );
    }

    public static CommandElement nation() {
        return GenericArguments.choices(
                Text.of("nation"),
                () -> AtherysTowns.getInstance().getNationRepository().getAllNations().stream().map(Nation::getName).collect(Collectors.toList()),
                string -> {
                    if (string.isEmpty()) return null;
                    return AtherysTowns.getInstance().getNationService().getNationFromName(string).get();
                }
        );
    }
}
