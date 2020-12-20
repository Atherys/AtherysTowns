package com.atherys.towns.facade;

import com.atherys.core.utils.UserUtils;
import com.atherys.towns.TownsConfig;
import com.atherys.towns.api.command.TownsCommandException;
import com.atherys.towns.model.entity.*;
import com.atherys.towns.service.PlotService;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.RoleService;
import com.atherys.towns.service.TownsPermissionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.*;

@Singleton
public class ResidentFacade {

    @Inject
    private ResidentService residentService;

    @Inject
    private RoleService roleService;

    @Inject
    private TownsPermissionService townsPermissionService;

    @Inject
    private PlotService plotService;

    @Inject
    private TownsMessagingFacade townsMsg;

    @Inject
    private NationFacade nationFacade;

    @Inject
    private TownFacade townFacade;

    @Inject
    private TownsConfig config;

    ResidentFacade() {
    }

    public void onLogin(Player player) {
        Resident resident = residentService.getOrCreate(player);
        residentService.setLastTownSpawn(resident, LocalDateTime.now());
        residentService.setLastLogin(resident, LocalDateTime.now());
        roleService.validateRoles(resident);
        townsPermissionService.updateContexts(player, resident);

        Town currentTown = plotService.getTownPlotByLocation(player.getLocation()).map(TownPlot::getTown).orElse(null);
        townsPermissionService.updateWorldContexts(player, currentTown);
    }

    public void onPlayerSpawn(RespawnPlayerEvent event) {
        Town town = residentService.getOrCreate(event.getOriginalPlayer()).getTown();

        if (town != null) {
            event.setToTransform(town.getSpawn());
        }
    }

    public void sendResidentInfo(Player player) {
        sendResidentInfo(player, residentService.getOrCreate(player));
    }

    public void sendResidentInfo(MessageReceiver receiver, User target) throws TownsCommandException {
        sendResidentInfo(receiver, residentService.getOrCreate(target));
    }

    public void sendResidentInfo(MessageReceiver receiver, Resident resident) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.UK)
                .withZone(ZoneId.systemDefault());

        boolean online = Sponge.getServer().getPlayer(resident.getUniqueId()).isPresent();
        Text status = online ? Text.of(GREEN, "Online") : Text.of(RED, "Offline");
        Text title = Text.of(GOLD, resident.getName(), DARK_GRAY, " (", status, DARK_GRAY, ")");
        String padding = StringUtils.repeat("=", townsMsg.getPadding(title.toPlain().length()));

        String lastPlayed = UserUtils.getUser(resident.getId())
                .flatMap(user -> user.get(Keys.LAST_DATE_PLAYED))
                .map(formatter::format)
                .orElse("N/A");

        Text.Builder residentInfo = Text.builder()
                .append(Text.of(DARK_GRAY, "[]", padding, "[ ", title, DARK_GRAY, " ]", padding, "[]", Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Town: ", townFacade.renderTown(resident.getTown()), Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "Last online: ", GOLD, lastPlayed, Text.NEW_LINE))
                .append(Text.of(DARK_GREEN, "First online: ", GOLD, formatter.format(resident.getRegisteredOn()), Text.NEW_LINE))
                .append(townsMsg.renderBank(resident.getId()));


        if (!resident.getFriends().isEmpty()) {
            residentInfo.append(Text.of(Text.NEW_LINE, DARK_GREEN, "Friends: ", GOLD, renderResidents(resident.getFriends())));
        }

        receiver.sendMessage(residentInfo.build());
    }

    public Text renderResident(Resident resident) {
        return Text.builder()
                .append(Text.of( isResidentActive(resident) ? GOLD : GRAY, resident.getName()))
                .onHover(TextActions.showText(
                        Text.of(
                                GOLD, resident.getName(), Text.NEW_LINE,
                                DARK_GREEN, "Town: ", GOLD, resident.getTown() == null ? "No Town" : resident.getTown().getName(), Text.NEW_LINE,
                                townsMsg.renderBank(resident.getId()), Text.NEW_LINE,
                                DARK_GRAY, "Click to view"
                        )
                ))
                .onClick(TextActions.executeCallback(source -> {
                    sendResidentInfo(source, resident);
                }))
                .build();
    }

    public boolean isResidentActive(Resident resident) {
        return Duration.between(resident.getLastLogin(), LocalDateTime.now()).compareTo(config.TAXES.INACTIVE_DURATION) < 0;
    }

    public Text renderResidents(Collection<Resident> residents) {
        Text.Builder residentsText = Text.builder();
        int i = 0;
        for (Resident resident : residents) {
            i++;
            residentsText.append(
                    Text.of(renderResident(resident), i == residents.size() || i == config.MAX_RESIDENTS_DISPLAY ? "" : ", ")
            );

            if (i == config.MAX_RESIDENTS_DISPLAY) {
                residentsText.append(Text.of(".."));
            }
        }

        return residentsText.build();
    }

    public void addResidentFriend(Player player, User target) throws TownsCommandException {
        if (player.getUniqueId().equals(target.getUniqueId())) {
            throw new TownsCommandException("Cannot add yourself as a friend.");
        }

        residentService.addResidentFriend(
                residentService.getOrCreate(player),
                residentService.getOrCreate(target)
        );

        townsMsg.info(player, GOLD, target.getName(), TextColors.DARK_GREEN, " was added as a friend.");
    }

    public void removeResidentFriend(Player player, User target) throws TownsCommandException {
        Resident resident = residentService.getOrCreate(player);
        Resident residentFriend = residentService.getOrCreate(target);

        if (resident.getFriends().contains(residentFriend)) {
            residentService.removeResidentFriend(resident, residentFriend);
            townsMsg.info(player, GOLD, target.getName(), TextColors.DARK_GREEN, " was removed from your friends.");
        } else {
            townsMsg.error(player, target.getName(), " is not your friend.");
        }
    }

    public Optional<Town> getPlayerTown(Player player) {
        return Optional.ofNullable(residentService.getOrCreate(player).getTown());
    }

    public Optional<Nation> getPlayerNation(Player player) {
        return getPlayerTown(player).map(Town::getNation);
    }
}
