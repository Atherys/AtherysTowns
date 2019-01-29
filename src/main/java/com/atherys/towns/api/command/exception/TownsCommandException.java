package com.atherys.towns.api.command.exception;

import com.atherys.towns.facade.TownsMessagingFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class TownsCommandException extends CommandException {
    public TownsCommandException(Text message) {
        super(Text.of(TownsMessagingFacade.PREFIX, TextColors.RED, message));
    }

    public TownsCommandException(Object... msg) {
        this(Text.of(msg));
    }

    public static TownsCommandException notPartOfTown() {
        return new TownsCommandException("You are not part of a town.");
    }
}
