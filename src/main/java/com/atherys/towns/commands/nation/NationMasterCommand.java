package com.atherys.towns.commands.nation;

import com.atherys.towns.commands.TownsMasterCommand;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class NationMasterCommand extends TownsMasterCommand {

    private static final NationMasterCommand instance = new NationMasterCommand();

    private NationMasterCommand () {
        addChild( NationCreateCommand.getInstance().getSpec(), "create" );
        addChild( NationInfoCommand.getInstance().getSpec(), "info" );
        addChild( NationDepositCommand.getInstance().getSpec(), "deposit" );
        addChild( NationWithdrawCommand.getInstance().getSpec(), "deposit" );
    }

    @Override
    protected CommandResult execute(Player player, CommandContext args, Resident resident, @Nullable Town town, @Nullable Nation nation) {
        showHelp("n", player);
        return CommandResult.empty();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description(Text.of("Master Nation command."))
                .children(getChildren())
                .executor(this)
                .build();
    }

    public static NationMasterCommand getInstance() {
        return instance;
    }
}
