package com.atherys.towns.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class Question {

    private static final Text QUESTION_DECORATION_TOP = Text.of(TextColors.DARK_AQUA, "{", TextColors.AQUA, "Question", TextColors.DARK_AQUA, "}\n");
    private static final Text QUESTION_DECORATION_BOT = Text.of(TextColors.DARK_AQUA, "\n");

    private static Map<UUID,Question> questions = new HashMap<>();

    public static class Builder {

        private Question question;

        private Builder( Text question ) {
            this.question = new Question( question );
        }

        public Builder addAnswer ( Answer answer ) {
            question.addAnswer( answer );
            return this;
        }

        public Question build() {
            return question;
        }

    }

    public static class Answer {

        private Text text;
        private Consumer<Player> action;

        private Answer(Text text) {
            this.text = text;
        }

        private Answer(Text text, Consumer<Player> action) {
            this.text = text;
            this.action = action;
        }

        public static Answer of(Text name, Consumer<Player> action) {
            return new Answer(name, action);
        }

        public Text getText() {
            return text;
        }

        Consumer<Player> getAction() {
            return action;
        }

        public void setAction(Consumer<Player> action) {
            this.action = action;
        }

        public void execute(Player source) {
            action.accept(source);
        }
    }


    private UUID id;
    private Text question;
    private List<Answer> answers;

    Question ( Text question ) {
        this.question = question;
        this.answers = new LinkedList<>();
        this.id = UUID.randomUUID();
    }

    public static Builder of(Text question) {
        return new Builder( question );
    }

    public UUID getId() {
        return id;
    }

    public Text getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    void addAnswer ( Answer answer ) {
        this.answers.add(answer);
    }

    public Text asText() {
        Text.Builder builder = Text.builder();
        builder.append( QUESTION_DECORATION_TOP );
        builder.append( question );
        builder.append( Text.of("\n") );

        for ( Answer answer : answers ) {
            builder.append( Text.of( TextStyles.RESET, TextColors.RESET, "[" ) );
            builder.append( Text.builder().append( answer.getText() )
                    .onHover( TextActions.showText( Text.of("Click to answer") ) )
                    .onClick( TextActions.executeCallback(source -> {
                        if ( !(source instanceof Player) ) {
                            source.sendMessage( Text.of(TextColors.RED, "Must be a player to reply to a question.") );
                            return;
                        }

                        if ( questions.containsKey( this.id ) ) {
                            answer.execute( (Player) source );
                            questions.remove( this.id );
                        } else {
                            source.sendMessage( Text.of(TextColors.RED, "You have already responded to that question!") );
                        }
                    }
                    ))
                    .build() );
            builder.append( Text.of( TextStyles.RESET, TextColors.RESET, "]\n" ) );
        }

        builder.append( QUESTION_DECORATION_BOT );
        return builder.build();
    }

    public void pollChat ( @Nonnull Player player ) {
        questions.put( id, this );
        player.sendMessage( this.asText() );
    }

    public void pollBook ( @Nonnull Player player ) {
        questions.put( id, this );
        player.sendBookView( BookView.builder().addPage( asText() ).build() );
    }

    public void pollViewButton ( @Nonnull Player player, @Nonnull Text buttonText ) {
        questions.put( id, this );
        Text text = Text.builder()
                .append(buttonText)
                .onHover(TextActions.showText(Text.of(TextColors.AQUA, "Click to View")))
                .onClick(TextActions.executeCallback(source -> { if ( source instanceof Player ) this.pollBook((Player) source); }))
                .build();

        player.sendMessage( text );
    }
}
