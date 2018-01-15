package com.atherys.towns.commands.town;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.commands.TownsSimpleCommand;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.permissions.actions.TownActions;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Chunk;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TownCreateCommand extends TownsSimpleCommand {

    private static TownCreateCommand instance = new TownCreateCommand();

    public static TownCreateCommand getInstance() {
        return instance;
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        if ( resident.getTown().isPresent() ) {
            TownMessage.warn(player, Text.of("You are already part of a town. Please leave your current town first in order to create a new one.") );
            return CommandResult.empty();
        }

        Optional<PlotDefinition> define;

        try {
            define = PlotDefinition.fromPlayer(player, null);
        } catch (PlotDefinition.DefinitionNotValidException e) {
            TownMessage.warn(player, Text.of("Your plot definition did not meet the requirements. You may clear your selection with ", TextStyles.BOLD, " /p desel", TextStyles.RESET, TextColors.RED, " and try creating a town from the chunk you are standing in."));
            return CommandResult.empty();
        } catch (PlotDefinition.DefinitionNotPresentException e) {
            Optional<Chunk> chunk = player.getLocation().getExtent().getChunkAtBlock(player.getLocation().getBlockPosition());
            if ( !chunk.isPresent() ) {
                TownMessage.warn(player, "Plugin could not find chunk. You must therefore create a plot definition first in order to create the town. This will become the Home plot of your town, and the location where you are standing ( inside of the definition ) will become the spawn point of the town." );
                return CommandResult.empty();
            } else {
                try {
                    define = PlotDefinition.fromChunk(player, null, chunk.get());
                    TownMessage.inform(player, "Creating town from chunk...");
                } catch (PlotDefinition.DefinitionNotValidException | PlotDefinition.DefinitionNotPresentException e1) {
                    return CommandResult.empty();
                }
            }
        }

        if ( define.isPresent() ) {
            Optional<Nation> n = NationManager.getInstance().getByName( args.<String>getOne("nation").orElse(UUID.randomUUID().toString()));
            Town t = Town.create(define.get(), resident, args.<String>getOne(Text.of("townName")).orElse(player.getName() + "'s Town"), AtherysTowns.getConfig().TOWN.INITIAL_AREA );
            if ( n.isPresent() ) t.setParent(n.get());
            else TownMessage.warn(player, "The nation you specified was invalid. Town was not added to a nation.");

            t.createView().ifPresent( view -> view.show(player) );
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Used to create a new town. If you are already part of a town, you must leave your current town first." ) )
                .permission( TownActions.CREATE_TOWN.getPermission() )
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("townName"))),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("nation")))
                )
                .executor( this )
                .build();
    }
}
