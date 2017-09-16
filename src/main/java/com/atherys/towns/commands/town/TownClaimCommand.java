package com.atherys.towns.commands.town;

import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotDefinition;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;

import javax.annotation.Nullable;
import java.util.Optional;

public class TownClaimCommand extends AbstractTownCommand {

    TownClaimCommand() {
        super(
                new String[] { "claim" },
                "claim [claimChunk?]",
                Text.of("Used to claim a new plot for the town."),
                TownRank.Action.CLAIM_PLOT,
                true,
                false,
                true,
                true
        );

    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {

        PlotDefinition define;

        if ( town != null ) {

            if ( !args.hasAny("claimChunk?") ) {
                Optional<PlotDefinition> definitionOptional;

                try {
                    definitionOptional = PlotDefinition.fromPlayer(player, town);
                } catch (PlotDefinition.DefinitionNotValidException | PlotDefinition.DefinitionNotPresentException e) {
                    return CommandResult.empty();
                }

                if ( definitionOptional.isPresent() ) {
                    define = definitionOptional.get();
                } else {
                    TownMessage.warn( player, Text.of("Definition invalid. Claim cancelled."));
                    return CommandResult.empty();
                }

            } else {
                // claim chunk
                Optional<Chunk> chunk = player.getLocation().getExtent().getChunkAtBlock( player.getLocation().getBlockPosition() );
                if ( chunk.isPresent() ) {
                    try {
                        Optional<PlotDefinition> defineOpt = PlotDefinition.fromChunk( player, town, chunk.get() );
                        if ( defineOpt.isPresent() ) {
                            define = defineOpt.get();
                        } else {
                            TownMessage.warn(player, Text.of("Could not create plot from chunk."));
                            return CommandResult.empty();
                        }
                    } catch (PlotDefinition.DefinitionNotValidException | PlotDefinition.DefinitionNotPresentException e) {
                        return CommandResult.empty();
                    }
                } else {
                    TownMessage.warn( player, "Plugin could not find chunk. You must therefore create a plot definition." );
                    return CommandResult.empty();
                }
            }

            if ( define.area() + town.area() > town.maxSize() ) {
                TownMessage.warn( player, "Your town cannot grow any larger than ", town.maxSize(), " blocks in area!" );
                return CommandResult.empty();
            }

            Plot p = Plot.create(define, town, "None");
            town.claimPlot(p);
            TownMessage.inform(player, "Plot Claimed.");
            return CommandResult.success();
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .permission("atherys.towns.commands.town.claim")
                .description(Text.of( "Used to claim a new plot for your town!" ) )
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("claimChunk?")))
                )
                .executor(this)
                .build();
    }
}
