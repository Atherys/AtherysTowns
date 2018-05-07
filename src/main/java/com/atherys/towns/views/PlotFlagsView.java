package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.plot.PlotFlags;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PlotFlagsView implements View<PlotFlags> {

    private final PlotFlags plotFlags;

    public PlotFlagsView(PlotFlags plotFlags) {
        this.plotFlags = plotFlags;
    }

    @Override
    public void show(Player player) {
        player.sendMessage(formatted());
    }

    public void showSingleLine(Player player) {
        player.sendMessage(formattedSingleLine());
    }

    public void showDifferences(Player player, PlotFlags otherFlags) {
        player.sendMessage(differencesFormatted(otherFlags));
    }

    public Text formatted() {
        Text.Builder textBuilder = Text.builder();
        plotFlags.getAll().forEach((k, v) -> textBuilder.append(
            Text.of(AtherysTowns.getConfig().COLORS.PRIMARY, k.getName(),
                AtherysTowns.getConfig().COLORS.DECORATION, " ( ",
                AtherysTowns.getConfig().COLORS.TEXT, v.getName(),
                AtherysTowns.getConfig().COLORS.DECORATION, " ) \n")));
        return textBuilder.build();
    }

    public Text formattedSingleLine() {
        Text.Builder textBuilder = Text.builder();
        plotFlags.getAll().forEach((k, v) -> textBuilder.append(
            Text.of(AtherysTowns.getConfig().COLORS.PRIMARY, k.getName(),
                AtherysTowns.getConfig().COLORS.DECORATION, " ( ",
                AtherysTowns.getConfig().COLORS.TEXT, v.getName(),
                AtherysTowns.getConfig().COLORS.DECORATION, " ); ")));
        return textBuilder.build();
    }

    public Text differencesFormatted(PlotFlags flags) {
        Text.Builder diffBuilder = Text.builder();
        diffBuilder.append(Text.of(AtherysTowns.getConfig().COLORS.DECORATION, ".o0o.-{ "));
        flags.getAll().forEach((k, v) -> {
            if (!flags.get(k).equals(this.plotFlags.get(k))) {
                diffBuilder.append(
                    Text.of(AtherysTowns.getConfig().COLORS.PRIMARY, k.getName(), " ( ",
                        AtherysTowns.getConfig().COLORS.WARNING, v.getName(),
                        AtherysTowns.getConfig().COLORS.PRIMARY, " ); ", TextColors.RESET));
            }
        });
        diffBuilder.append(Text.of(AtherysTowns.getConfig().COLORS.DECORATION, " }-.o0o."));
        return diffBuilder.build();
    }
}
