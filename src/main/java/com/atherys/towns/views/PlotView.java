package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.town.Town;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class PlotView implements View<Plot> {

  private final Plot plot;

  public PlotView(Plot plot) {
    this.plot = plot;
  }

  @Override
  public void show(Player player) {
    player.sendMessage(toText());
  }

  public Text toText() {

    Town parent = plot.getParent().get();

    String nationName = "None";
    if (parent.getParent().isPresent()) {
      nationName = parent.getParent().get().getName();
    }

    TextColor decoration = AtherysTowns.getConfig().COLORS.DECORATION;
    TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
    TextColor tertiary = AtherysTowns.getConfig().COLORS.TERTIARY;
    TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

    return Text.builder()
        .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
        .append(Text.of(tertiary, TextStyles.BOLD, plot.getName(), TextStyles.RESET))
        .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Town: ", TextStyles.RESET,
            parent.getColor(), parent.getName(), primary, " ( ", textColor, nationName,
            textColor, " )\n"))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Flags: ", TextStyles.RESET,
            new PlotFlagsView(plot.getFlags()).formattedSingleLine(), "\n"))
        .build();
  }
}
