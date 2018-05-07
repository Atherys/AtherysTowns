package com.atherys.towns.views;

import com.atherys.core.views.View;
import com.atherys.towns.AtherysTowns;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import com.atherys.towns.town.Town;
import com.atherys.towns.utils.FormatUtils;
import java.util.List;
import java.util.Optional;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class NationView implements View<Nation> {

    private final Nation nation;

    public NationView(Nation nation) {
        this.nation = nation;
    }

    @Override
    public void show(Player player) {
        player.sendMessage(toText());
    }

    public Text toText() {

        String leaderName = AtherysTowns.getConfig().TOWN.NPC_NAME;

        Optional<Town> capital = nation.getCapital();
        if (capital.isPresent()) {
            Optional<Resident> leader = capital.get().getMayor();
            if (leader.isPresent()) {
                leaderName = leader.get().getName();
            }
        }

        List<Town> towns = nation.getTowns();

        TextColor decoration = AtherysTowns.getConfig().COLORS.DECORATION;
        TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
        TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

        return Text.builder()
            .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
            .append(Text.of(nation.getColor(), TextStyles.BOLD, nation.getName(), TextStyles.RESET))
            .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
            .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Description: ",
                TextStyles.RESET, textColor, nation.getDescription(), "\n"))
            .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET,
                textColor, FormatUtils.getFormattedBank(nation), "\n"))
            .append(
                Text.of(TextColors.RESET, primary, TextStyles.BOLD, nation.getLeaderTitle(), ": ",
                    TextStyles.RESET, textColor, leaderName + "\n"))
            .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Towns[", textColor,
                towns.size(), primary, "]: ", TextStyles.RESET, TextColors.RESET,
                getFormattedTowns(towns)))
            .build();
    }

    public Text getFormattedTowns(List<Town> towns) {
        Text.Builder townsBuilder = Text.builder();
        Text separator = Text.of(", ");
        for (Town t : towns) {
            Text resText = Text.builder()
                .append(Text.of(t.getName()))
                .onHover(TextActions.showText(
                    Text.of(AtherysTowns.getConfig().COLORS.SECONDARY, "Click for more info!")))
                .onClick(TextActions.runCommand("/town info " + t.getName()))
                .build();

            townsBuilder.append(resText, separator);
        }
        return townsBuilder.build();
    }

    public Text getFormattedName() {

        String leaderName = AtherysTowns.getConfig().TOWN.NPC_NAME;

        Optional<Town> capital = nation.getCapital();
        if (capital.isPresent()) {
            Optional<Resident> leader = capital.get().getMayor();
            if (leader.isPresent()) {
                leaderName = leader.get().getName();
            }
        }

        TextColor decoration = AtherysTowns.getConfig().COLORS.DECORATION;
        TextColor primary = AtherysTowns.getConfig().COLORS.PRIMARY;
        TextColor textColor = AtherysTowns.getConfig().COLORS.TEXT;

        Text hoverText = Text.builder()
            .append(Text.of(decoration, ".o0o.______.[ ", TextColors.RESET))
            .append(Text.of(nation.getColor(), TextStyles.BOLD, nation.getName(), TextStyles.RESET))
            .append(Text.of(TextColors.RESET, decoration, " ].______.o0o.\n", TextColors.RESET))
            .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Description: ",
                TextStyles.RESET, textColor, nation.getDescription(), "\n"))
            .append(Text.of(TextColors.RESET, primary, TextStyles.BOLD, "Bank: ", TextStyles.RESET,
                textColor, FormatUtils.getFormattedBank(nation), "\n"))
            .append(
                Text.of(TextColors.RESET, primary, TextStyles.BOLD, nation.getLeaderTitle(), ": ",
                    TextStyles.RESET, textColor, leaderName + "\n"))
            .build();

        return Text.builder().append(Text.of(nation.getColor(), nation.getName()))
            .onHover(TextActions.showText(hoverText)).build();
    }
}
