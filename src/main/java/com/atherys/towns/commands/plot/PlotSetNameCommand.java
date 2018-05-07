package com.atherys.towns.commands.plot;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsCommand;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
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

@Aliases("name")
@Description("Used to change the name of the plot you are currently standing on")
@Permission("atherystowns.plot.set.name")
public class PlotSetNameCommand extends TownsCommand implements ParameterizedCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident,
                                    @Nullable Town town, @Nullable Nation nation) {
        Optional<Plot> plotOpt = PlotManager.getInstance().getByLocation(player.getLocation());
        if (!plotOpt.isPresent()) {
            TownMessage.warn(player,
                    "You must be standing within the borders of a town plot in order to do this command.");
            return CommandResult.empty();
        }

        Plot plot = plotOpt.get();
        if (!plot.getTown().equals(town)) {
            TownMessage.warn(player,
                    "You must be standing within the borders of a plot which belongs to your own town in order to do this command.");
            return CommandResult.empty();
        }

        Optional<String> name = args.getOne("name");
        if (!name.isPresent() || name.get().length() > AtherysTowns
                .getConfig().TOWN.MAX_NAME_LENGTH) {
            TownMessage.warn(player, "You must provide a valid name no longer than ",
                    AtherysTowns.getConfig().TOWN.MAX_NAME_LENGTH, " symbols.");
            return CommandResult.empty();
        }

        town.informResidents(Text.of("Plot ", plot.getName(), " is being renamed to ", name.get()));
        plot.setName(name.get());

        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.string(Text.of("name"))
        };
    }
}
