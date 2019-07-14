package com.atherys.towns.facade;

import com.atherys.core.utils.UserUtils;
import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.entity.Nation;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.entity.Town;
import com.atherys.towns.service.ResidentService;
import com.atherys.towns.service.TownService;
import com.flowpowered.noise.module.modifier.ScalePoint;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Optional;

@Singleton
public class ResidentFacade {

    @Inject
    ResidentService residentService;

    @Inject
    TownService townService;

    ResidentFacade() {
    }

    public void sendResidentInfo(Player player) {
        sendResidentInfo(player, residentService.getOrCreate(player));
    }

    public void sendResidentInfo(MessageReceiver receiver, String resident) throws TownsCommandException {
        User user = UserUtils.getUser(resident).orElseThrow(() -> {
            return new TownsCommandException("No player with name ", resident, " found.");
        });

        sendResidentInfo(receiver, residentService.getOrCreate(user));
    }

    public void sendResidentInfo(MessageReceiver receiver, Resident resident) {
        Text.Builder residentInfo = Text.builder()
                .append(Text.of("Name: ", resident.getName()), Text.NEW_LINE)
                .append(Text.of("Town: ", resident.getTown() == null ? resident.getTown().getName() : "No town", Text.NEW_LINE))
                .append(Text.of("Last online: ", resident.getLastLogin().toString(), Text.NEW_LINE))
                .append(Text.of("First online: ", resident.getRegisteredOn().toString(), Text.NEW_LINE));

        boolean online = Sponge.getServer().getPlayer(resident.getUniqueId()).isPresent();
        residentInfo.append(Text.of("Online: ", online ? "Yes" : "No", Text.NEW_LINE));

        residentInfo.append(Text.of("Friends: "));
        resident.getFriends().forEach(friend -> residentInfo.append(Text.of(friend.getName(), ", ")));

        receiver.sendMessage(residentInfo.build());
    }

    public boolean isPlayerInTown(Player player, Town town) {
        return town.equals(residentService.getOrCreate(player).getTown());
    }

    public boolean isPlayerInNation(Player player, Nation nation) {
        Resident resident = residentService.getOrCreate(player);

        if (resident.getTown() == null) {
            return false;
        }

        return nation.equals(resident.getTown().getNation());
    }

    public Optional<Town> getPlayerTown(Player player) {
        return Optional.ofNullable(residentService.getOrCreate(player).getTown());
    }

    public Optional<Nation> getPlayerNation(Player player) {
        return getPlayerTown(player).map(Town::getNation);
    }
}
