package com.atherys.towns.util;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.config.NationRoleConfig;
import com.atherys.towns.config.TownRoleConfig;
import com.atherys.towns.model.Nation;
import com.atherys.towns.model.entity.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.*;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
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
                () -> AtherysTowns.getInstance().getNationService().getNations().keySet(),
                string -> {
                    if (string.isEmpty()) return null;
                    return AtherysTowns.getInstance().getNationService().getNationFromId(string).get();
                }
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
