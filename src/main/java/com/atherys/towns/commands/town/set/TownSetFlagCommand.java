package com.atherys.towns.commands.town.set;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.flags.Extent;
import com.atherys.towns.plot.flags.Flag;
import com.atherys.towns.plot.flags.FlagRegistry;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

@Aliases("flag")
@Description("Used to change a town flag")
@Permission("atherystowns.town.set.flag")
public class TownSetFlagCommand extends TownsCommand implements ParameterizedCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        if (town == null) {
            return CommandResult.empty();
        }

        Optional<Flag> flag = args.getOne("flag");

        if (!flag.isPresent()) {
            TownMessage.warn(player, "You must provide a valid flag. Possible flags: ",
                    AtherysTowns.getConfig().COLORS.PRIMARY, FlagRegistry.getInstance().getAll());
            return CommandResult.empty();
        }

        Optional<Extent> extent = args.getOne("extent");
        if (!extent.isPresent()) {
            TownMessage.warn(player, "You must provide a valid extent. Possible extents: ",
                    AtherysTowns.getConfig().COLORS.PRIMARY, FlagRegistry.getInstance().getAll());
            return CommandResult.empty();
        }

        if (player.hasPermission(flag.get().getAction().getPermission())) {

            Extent ext = extent.get();

            if (!flag.get().checkExtent(ext)) {
                TownMessage.warn(player, "You cannot use the ", ext.getName(), " extent with the ",
                        flag.get().getName(), " flag");
                return CommandResult.empty();
            }

            town.setFlag(flag.get(), ext);
            town.informResidents(
                    Text.of("Town flag " + flag.get().getName() + " changed to " + ext.getName()));

            return CommandResult.success();
        } else {
            TownMessage.warn(player,
                    "Your town rank does not permit you to change the " + flag.get().getName()
                            + " flag.");
            return CommandResult.empty();
        }
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.catalogedElement(Text.of("flag"), Flag.class),
                GenericArguments.catalogedElement(Text.of("extent"), Extent.class)
        };
    }
}
