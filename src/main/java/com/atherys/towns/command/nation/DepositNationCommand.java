package com.atherys.towns.command.nation;

import com.atherys.core.command.ParameterizedCommand;
import com.atherys.core.command.PlayerCommand;
import com.atherys.core.command.annotation.Aliases;
import com.atherys.core.command.annotation.Description;
import com.atherys.core.command.annotation.Permission;
import com.atherys.towns.AtherysTowns;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

@Aliases("deposit")
@Description("Deposits funds into the nation.")
@Permission("atherystowns.nation.deposit")
public class DepositNationCommand implements PlayerCommand, ParameterizedCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.bigDecimal(Text.of("amount"))
        };
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        AtherysTowns.getInstance().getNationFacade().depositToNation(source, args.<BigDecimal>getOne("amount").get());
        return CommandResult.success();
    }
}
