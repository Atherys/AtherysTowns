package com.atherys.towns2.resident;


import com.atherys.towns2.nation.Nation;
import com.atherys.towns2.town.Town;
import com.atherys.towns2.util.CauseUtils;
import com.atherys.towns2.util.UserUtils;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.ryanhamshire.griefprevention.api.claim.TrustType;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Identifiable;

public class Resident implements Identifiable {

    private UUID uuid;

    private Town town;

    public Resident() {

    }

    public Resident(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTown(Town town) {
        this.town = town;
        town.getPlots().forEach(plot -> plot.getClaim().addUserTrust(this.uuid, TrustType.MANAGER, CauseUtils
            .of(this, plot)));
    }

    public Optional<Town> getTown() {
        return Optional.ofNullable(this.town);
    }

    public Optional<Nation> getNation() {
        return getTown().flatMap(Town::getNation);
    }

    @Override
    @Nonnull
    public UUID getUniqueId() {
        return uuid;
    }

    public Optional<User> asUser() {
        return UserUtils.getUser(this.uuid);
    }
}
