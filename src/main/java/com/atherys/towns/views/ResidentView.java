package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.FormatUtils;
import java.text.SimpleDateFormat;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class ResidentView implements View<Resident> {

  private final Resident resident;

  public ResidentView(Resident resident) {
    this.resident = resident;
  }

  @Override
  public void show(Player player) {
    player.sendMessage(toText());
  }

  public Text toText() {

    Text townText = Text.of("None");
    Text nationText = Text.of("None");

    if (resident.getTown().isPresent()) {
      Town town = resident.getTown().get();
      townText = new TownView(town).getFormattedName();

      if (town.getParent().isPresent()) {
        nationText = new NationView(town.getParent().get()).getFormattedName();
      }
    }

    TextColor decoration = AtherysTowns.getConfig().COLORS.DECORATION;
    TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
    TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

    return Text.builder()
        .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
        .append(Text.of(textColor, TextStyles.BOLD, resident.getName(), TextStyles.RESET))
        .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Registered: ",
            TextStyles.RESET, textColor, getFormattedRegisterDate(), "\n"))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Last Online: ",
            TextStyles.RESET, textColor,
            resident.isOnline() ? Text.of(TextColors.GREEN, TextStyles.BOLD, "Now")
                : Text.of(TextColors.RED, getFormattedLastOnlineDate()), "\n"))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET,
            textColor, FormatUtils.getFormattedBank(resident), "\n"))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Town: ", TextStyles.RESET,
            townText, decoration, " ( ", textColor, nationText, decoration, " )\n"))
        .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Rank: ", TextStyles.RESET,
            textColor,
            resident.getTownRank() == null ? "None" : resident.getTownRank().getName()))
        .build();
  }

  public String getFormattedRegisterDate() {
    return new SimpleDateFormat(AtherysTowns.getConfig().TOWN.DATE_FORMAT)
        .format(resident.getRegisteredDate());
  }

  public String getFormattedLastOnlineDate() {
    return new SimpleDateFormat(AtherysTowns.getConfig().TOWN.DATE_FORMAT)
        .format(resident.getLastOnlineDate());
  }
}
