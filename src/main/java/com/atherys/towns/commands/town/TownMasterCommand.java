package com.atherys.towns.commands.town;

import com.atherys.towns.commands.TownsMasterCommand;
import com.atherys.towns.commands.town.set.TownSetMasterCommand;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class TownMasterCommand extends TownsMasterCommand {

    private static TownMasterCommand instance = new TownMasterCommand();

    private TownMasterCommand () {
        addChild( TownHelpCommand.getInstance().getSpec(), "help" );
        addChild( TownHereCommand.getInstance().getSpec(), "here" );
        addChild( TownInfoCommand.getInstance().getSpec(), "info" );
        addChild( TownCreateCommand.getInstance().getSpec(), "create" );
        addChild( TownJoinCommand.getInstance().getSpec(), "join" );
        addChild( TownLeaveCommand.getInstance().getSpec(), "leave" );
        addChild( TownKickCommand.getInstance().getSpec(), "kick" );
        addChild( TownSpawnCommand.getInstance().getSpec(), "spawn", "home" );
        addChild( TownDepositCommand.getInstance().getSpec(), "deposit" );
        addChild( TownWithdrawCommand.getInstance().getSpec(), "withdraw" );
        addChild( TownBorderCommand.getInstance().getSpec(), "border" );
        addChild( TownClaimCommand.getInstance().getSpec(), "claim" );
        addChild( TownUnclaimCommand.getInstance().getSpec(), "unclaim" );
        addChild( TownInviteCommand.getInstance().getSpec(), "invite" );
        addChild( TownRuinCommand.getInstance().getSpec(), "ruin", "destroy" );
        addChild( TownSetMasterCommand.getInstance().getSpec(), "set" );
    }

    public static TownMasterCommand getInstance () {
        return instance;
    }

    @Override
    protected CommandResult execute ( Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation ) {
        if ( resident.getTown().isPresent() ) {
            resident.getTown().get().createView().show( player );
        } else {
            TownMessage.warn( player, "You are not part of a town." );
        }
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec () {
        return CommandSpec.builder()
                .description( Text.of( "The master town command." ) )
                .executor( this )
                .children( getChildren() )
                .build();
    }

}
