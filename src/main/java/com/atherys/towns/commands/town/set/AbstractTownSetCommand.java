package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.AbstractCommand;
import com.atherys.towns.commands.town.AbstractTownCommand;
import com.atherys.towns.commands.town.TownMasterCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.resident.ranks.TownRank;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class AbstractTownSetCommand extends AbstractTownCommand {

    private static final AbstractTownSetCommand instance = new AbstractTownSetCommand(true);

    private AbstractTownSetCommand( boolean instance ) {
        super(new String[] { "set" }, "set", Text.of("Master Town Set Command."), TownRank.Action.NONE, true, false, false, true);
    }

    public AbstractTownSetCommand() {
        super( new String[] { "set" }, "set", Text.of("Master Town Set Command."), TownRank.Action.NONE, true, false, false, true);

        new TownSetColorCommand();
        new TownSetDescriptionCommand();
        new TownSetFlagCommand();
        new TownSetMayorCommand();
        new TownSetMOTDCommand();
        new TownSetNationCommand();
        new TownSetNameCommand();
        new TownSetRankCommand();
        new TownSetSpawnCommand();

        TownMasterCommand.getInstance().getChildren().add(AbstractTownSetCommand.instance);
    }

    protected AbstractTownSetCommand(String[] aliases, String syntax, Text description, TownRank.Action action ) {
        super(aliases, "set " + syntax, description, action, true, false, true, false);
        instance.getChildren().add(this);
    }

    @Override
    public CommandSpec getSpec() {
        CommandSpec.Builder spec = CommandSpec.builder()
                .permission("atherys.towns.commands.set")
                .description(Text.of("Master Town Set Command."))
                .executor(this);

        for ( AbstractCommand cmd : getChildren() ) {
            spec.child( cmd.getSpec(), cmd.getAliases() );
        }

        return spec.build();
    }

    @Override
    public CommandResult townsExecute(@Nullable Nation nation, @Nullable Town town, Resident resident, Player player, CommandContext args) {
        for ( AbstractCommand tcmd : getChildren() ) {
            tcmd.sendInfo(player);
        }
        return CommandResult.success();
    }
}
