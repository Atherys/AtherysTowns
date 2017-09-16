package com.atherys.towns.utils;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Question {

    private static final Text questionDecorationTop = Text.of(TextColors.DARK_AQUA, "{", TextColors.AQUA, "Question", TextColors.DARK_AQUA, "}\n");
    private static final Text questionDecorationBottom = Text.of(TextColors.DARK_AQUA, "\n");

    // TODO: Figure out question tracking
    private static final Map<UUID,Text> playerQuestionMap = new HashMap<>();

    public enum Type {
        YES_NO("Yes", "No"),
        CONFIRM_CANCEL("Confirm", "Cancel"),
        APPROVE_DENY("Approve", "Deny"),
        ACCEPT_REFUSE("Accept", "Refuse");

        Text yes;
        Text no;

        Type ( String yes, String no ) {
            this.yes = Text.of(TextColors.DARK_GREEN, TextStyles.BOLD, yes);
            this.no = Text.of(TextColors.DARK_RED, TextStyles.BOLD, no);
        }

        public Text getYesText() { return yes; }
        public Text getNoText()  { return no; }
    }

    private static Consumer<CommandSource> finalAction = commandSource -> {
        playerQuestionMap.remove( ( (Player) commandSource ).getUniqueId() );
    };

    /**
     *
     * @param player The player who will answer the question
     * @param question The question to be asked
     * @param type the type of answers
     * @param yes what happens if the player selects the "yes" option
     * @param no what happens if the player selects the "no" option
     * @return the question as transformed into text.
     */
    public static Text poll (Player player, Text question, Type type, Consumer<CommandSource> yes, Consumer<CommandSource> no ) {
        Text q = asText(question, type, yes, no);
        playerQuestionMap.put(player.getUniqueId(), q );
        player.sendMessage(q);
        return q;
    }

    /**
     *
     * @param player The player who will answer the question
     * @param question The question to be asked
     * @param type the type of answers
     * @param yes what happens if the player selects the "yes" option
     * @return the question as transformed into text.
     */
    public static Text poll ( Player player, Text question, Type type, Consumer<CommandSource> yes ) {
        Text q = asText(question, type, yes );
        playerQuestionMap.put(player.getUniqueId(), q );
        player.sendMessage(q);
        return q;
    }

    /**
     *
     * @param question The question to be asked
     * @param type the type of answers
     * @param yes what happens if the player selects the "yes" option
     * @param no what happens if the player selects the "no" option
     * @return the question as transformed into text.
     */
    public static Text asText ( Text question, Type type, Consumer<CommandSource> yes, Consumer<CommandSource> no ) {
        return Text.builder()
                .append(questionDecorationTop)
                .append(question, Text.of("\n\n"))
                .append(Text.of(TextStyles.RESET, TextColors.RESET, "["))
                .append(getYesAnswer(type, yes))
                .append(Text.of(TextStyles.RESET, TextColors.RESET, "]["))
                .append(getNoAnswer(type, no))
                .append(Text.of(TextStyles.RESET, TextColors.RESET, "]\n"))
                .append(questionDecorationBottom)
                .build();
    }

    /**
     *
     * @param question The question to be asked
     * @param type the type of answers
     * @param yes what happens if the player selects the "yes" option
     * @return the question as transformed into text.
     */
    public static Text asText ( Text question, Type type, Consumer<CommandSource> yes ) {
        return asText(question, type, yes, (commandSource -> {} ) );
    }

    public static Text getYesAnswer ( Type type, Consumer<CommandSource> yes ) {
        return type.getYesText().toBuilder()
                .onClick( TextActions.executeCallback(yes.andThen(finalAction) ) )
                .onHover( TextActions.showText( Text.of( "Click here to Agree" ) ) ).build();
    }

    public static Text getNoAnswer ( Type type, Consumer<CommandSource> no ) {
        return type.getNoText().toBuilder()
                .onClick( TextActions.executeCallback(no.andThen(finalAction)) )
                .onHover( TextActions.showText( Text.of( "Click here to Refuse" ) ) ).build();
    }

    public static BookView asBookView ( Text question ) {
        return BookView.builder()
                .addPage(question)
                .build();
    }

    public static Text asViewButton ( Text question, Text buttonText ) {
        return Text.builder()
                .append(buttonText)
                .onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to View")))
                .onClick(TextActions.executeCallback(commandSource -> {
                    if ( commandSource instanceof Player ) {
                        ((Player) commandSource).sendBookView(asBookView(question));
                    }
                }))
                .build();
    }

}
