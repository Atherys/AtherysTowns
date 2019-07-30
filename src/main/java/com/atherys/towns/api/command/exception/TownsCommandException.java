package com.atherys.towns.api.command.exception;

import com.atherys.towns.entity.Town;
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

    public static TownsCommandException playerNotFound(String playerName) {
        return new TownsCommandException("No player with name ", playerName, " found.");
    }

    public static TownsCommandException townNotFound(String townName) {
        return new TownsCommandException("Town with name ", townName, " not found.");
    }

    public static TownsCommandException notPermittedForTown(String field) {
        return new TownsCommandException("You are not permitted to change the town ", field, ".");
    }

    public static TownsCommandException nationNotFound(String nationName) {
        return new TownsCommandException("Nation with name ", nationName, " not found.");
    }

    public static TownsCommandException notPermittedForNation(String field) {
        return new TownsCommandException("You are not permitted to change the nation ", field, ".");
    }
}
