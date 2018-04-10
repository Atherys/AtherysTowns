package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.permissions.ranks.TownRank;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.flags.Extents;
import com.atherys.towns.plot.flags.Flags;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.FormatUtils;
import com.atherys.towns.utils.ResidentUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TownView implements View<Town> {

    private final Town town;

    public TownView ( Town town ) {
        this.town = town;
    }

    @Override
    public void show ( Player player ) {
        player.sendMessage( toText() );
    }

    public Text toText () {

        Text nationName = Text.of( "None" );
        if ( town.getParent().isPresent() ) {
            nationName = new NationView( town.getParent().get() ).getFormattedName();
        }

        long plotSize = 0;
        for ( Plot p : town.getPlots() ) {
            plotSize += p.getDefinition().area();
        }

        String mayorName = AtherysTowns.getConfig().TOWN.NPC_NAME;
        Optional<Resident> mayor = town.getMayor();
        if ( mayor.isPresent() ) mayorName = mayor.get().getName();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance( Locale.US );
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator( ',' );
        formatter.setDecimalFormatSymbols( symbols );

        Text pvp = Text.of( TextStyles.BOLD, TextColors.GREEN, "( PvP On )" );
        if ( town.getTownFlags().get( Flags.PVP ) == Extents.NONE )
            pvp = Text.of( TextStyles.BOLD, TextColors.RED, "( PvP Off )" );

        TextColor decoration = AtherysTowns.getConfig().COLORS.DECORATION;
        TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
        TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

        return Text.builder()
                .append( Text.of( decoration, ".o0o.______.[ ", TextColors.RESET ) )
                .append( Text.of( town.getColor(), TextStyles.BOLD, town.getName(), TextStyles.RESET, " ", pvp ) )
                .append( Text.of( TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "MOTD: ", TextStyles.RESET, textColor, town.getMOTD(), "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Description: ", TextStyles.RESET, textColor, town.getDescription(), "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET, textColor, FormatUtils.getFormattedBank( town ), "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Flags: ", TextStyles.RESET, Text.of( TextStyles.ITALIC, TextColors.DARK_GRAY, "( Hover to view )", TextStyles.RESET ).toBuilder().onHover( TextActions.showText( new PlotFlagsView( town.getTownFlags() ).formatted() ) ).build(), "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Nation: ", TextStyles.RESET, textColor, nationName, "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Size: ", TextStyles.RESET, textColor, formatter.format( plotSize ), "/", formatter.format( town.getMaxSize() ), TextStyles.ITALIC, TextColors.DARK_GRAY, " ( ", formatter.format( town.getMaxSize() - plotSize ), " remaining )", "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Mayor: ", TextStyles.RESET, textColor, mayorName, "\n" ) )
                .append( Text.of( TextColors.RESET, primary, TextStyles.BOLD, "Residents[", textColor, town.getResidents().size(), primary, "]: ", TextStyles.RESET, TextColors.RESET, formatResidents() ) )
                .build();
    }

    private Text formatResidents () {
        List<Resident> residentsByLastOnline = ResidentUtils.sortResidentsByDate( town.getResidents() );
        Text.Builder residentsBuilder = Text.builder();
        Text separator = Text.of( ", " );

        TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
        TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

        int iterations = 0;
        for ( Resident r : residentsByLastOnline ) {
            if ( iterations == 25 ) break;
            TownRank tr = r.getTownRank();
            Text resText = Text.builder()
                    .append( Text.of( r.getName() ) )
                    .onHover( TextActions.showText( Text.of(
                            primary, TextStyles.BOLD, "Rank: ", TextStyles.RESET, textColor, tr.getName(), "\n",
                            primary, TextStyles.BOLD, "Last Online: ", TextStyles.RESET, textColor, new ResidentView( r ).getFormattedLastOnlineDate()

                    ) ) )
                    .onClick( TextActions.runCommand( "/res " + r.getName() ) )
                    .build();

            residentsBuilder.append( resText, separator );
            iterations++;
        }
        return residentsBuilder.build();
    }

    public Text getFormattedName () {
        return Text.builder().append( Text.of ( town.getColor(), town.getName() ) ).onHover( TextActions.showText( this.toText() ) ).build();
    }
}
