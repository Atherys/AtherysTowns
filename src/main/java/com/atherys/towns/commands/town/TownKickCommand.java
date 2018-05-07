package com.atherys.towns.commands.town;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.ranks.TownRanks;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

@Aliases("kick")
@Description("Used to kick a resident from the town.")
@Permission("atherystowns.town.kick")
public class TownKickCommand extends TownsCommand implements ParameterizedCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        Optional<User> user = args.getOne("resident");
        if (!user.isPresent()) {
            return CommandResult.empty();
        }

        if (town == null) {
            return CommandResult.empty();
        }
        Optional<Resident> res = town.getResident(user.get().getUniqueId());

        // if the resident is present and the rank of the resident trying to kick them is higher
        if (res.isPresent() && resident.getTownRank().isRankGreaterThan(res.get().getTownRank())
                || player.hasPermission("atherystowns.admin.kick_any")) {
            res.get().setTown(null, TownRanks.NONE);
            town.warnResidents(Text.of(user.get().getName(), " has been kicked from the town by ",
                    resident.getName()));
        }

        return CommandResult.empty();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.onlyOne(GenericArguments.user(Text.of("resident")))
        };
    }
}
