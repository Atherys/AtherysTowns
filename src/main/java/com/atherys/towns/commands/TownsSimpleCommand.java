package com.atherys.towns.commands;

import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public abstract class TownsSimpleCommand implements CommandExecutor {

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args)
        throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("Must be a player to execute this command."));
            return CommandResult.empty();
        }

        Player player = (Player) src;
        Resident res;
        @Nullable Town t = null;
        @Nullable Nation n = null;

        Optional<Resident> resOpt = ResidentManager.getInstance().get(player.getUniqueId());
        if (!resOpt.isPresent()) {
            return CommandResult.empty();
        } else {
            res = resOpt.get();
        }

        if (res.getTown().isPresent()) {
            t = res.getTown().get();
        }

        if (t != null && t.getParent().isPresent()) {
            n = t.getParent().get();
        }

        return execute(player, args, res, t, n);
    }

    protected abstract CommandResult execute(Player player, CommandContext args, Resident resident,
        @Nullable Town town, @Nullable Nation nation);

    public abstract CommandSpec getSpec();
}
