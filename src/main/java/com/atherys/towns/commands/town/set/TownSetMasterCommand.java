package com.atherys.towns.commands.town.set;

import com.atherys.towns.commands.TownsMasterCommand;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class TownSetMasterCommand extends TownsMasterCommand {

    private static TownSetMasterCommand instance = new TownSetMasterCommand();

    private TownSetMasterCommand() {
        addChild( TownSetColorCommand.getInstance().getSpec(), "color"                      );
        addChild( TownSetDescriptionCommand.getInstance().getSpec(), "description", "desc"  );
        addChild( TownSetNameCommand.getInstance().getSpec(), "name"                        );
        addChild( TownSetFlagCommand.getInstance().getSpec(), "flag"                        );
        addChild( TownSetMayorCommand.getInstance().getSpec(), "mayor"                      );
        addChild( TownSetMOTDCommand.getInstance().getSpec(), "motd"                        );
        addChild( TownSetRankCommand.getInstance().getSpec(), "rank"                        );
        addChild( TownSetNationCommand.getInstance().getSpec(), "nation"                    );
        addChild( TownSetSpawnCommand.getInstance().getSpec(), "spawn", "home"              );
    }

    public static TownSetMasterCommand getInstance() {
        return instance;
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description( Text.of( "Master Town Set command. Used to modify a town." ) )
                .executor( this )
                .children( getChildren() )
                .build();
    }
}
