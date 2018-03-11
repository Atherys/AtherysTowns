package com.atherys.towns.commands.wilderness;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.managers.WildernessManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class WildernessRegenCommand {

    private static WildernessRegenCommand instance = new WildernessRegenCommand();

    private static CommandExecutor EXECUTOR = ( src, args ) -> {
        long timestamp = System.currentTimeMillis();
        WildernessManager.getInstance().regenerate( timestamp );
        return CommandResult.empty();
    };

    public void register () {
        CommandSpec spec = CommandSpec.builder()
                .permission( "atherystowns.wilderness.regen" )
                .description( Text.of( "Wilderness Regen Command." ) )
                .executor( EXECUTOR )
                .build();

        Sponge.getCommandManager().register( AtherysTowns.getInstance(), spec, "wildregen" );
    }

    public static WildernessRegenCommand getInstance () {
        return instance;
    }

}
