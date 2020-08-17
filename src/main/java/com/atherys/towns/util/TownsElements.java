package com.atherys.towns.util;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.model.entity.Nation;
import com.atherys.towns.model.entity.Town;
import com.atherys.towns.service.PlotService;
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

    public static CommandElement allianceType() {
        Map<String, PlotService.AllianceType> allianceTypeMap = new HashMap<>();
        allianceTypeMap.put("ally", PlotService.AllianceType.Ally);
        allianceTypeMap.put("friend", PlotService.AllianceType.Friend);
        allianceTypeMap.put("enemy", PlotService.AllianceType.Enemy);
        allianceTypeMap.put("town", PlotService.AllianceType.Town);
        allianceTypeMap.put("neutral", PlotService.AllianceType.Neutral);

        return GenericArguments.choices(
                Text.of("type"),
                allianceTypeMap
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
