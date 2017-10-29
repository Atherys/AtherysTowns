package com.atherys.towns.commands.nation;

import com.atherys.towns.commands.TownsMasterCommand;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class NationMasterCommand extends TownsMasterCommand {

    private static final NationMasterCommand instance = new NationMasterCommand();

    private NationMasterCommand () {
        addChild( NationCreateCommand.getInstance().getSpec(), "create" );
        addChild( NationInfoCommand.getInstance().getSpec(), "info" );
        addChild( NationDepositCommand.getInstance().getSpec(), "deposit" );
        addChild( NationWithdrawCommand.getInstance().getSpec(), "deposit" );
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description(Text.of("Master Nation command."))
                .executor(this)
                .build();
    }

    public static NationMasterCommand getInstance() {
        return instance;
    }
}
