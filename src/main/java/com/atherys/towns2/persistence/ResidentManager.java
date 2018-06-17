package com.atherys.towns2.persistence;

import com.atherys.towns2.resident.Resident;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;

public class ResidentManager {

    private static ResidentManager instance = new ResidentManager();
    private Map<UUID, Resident> residents = new HashMap<>();

    public Resident getResident(Player player) {
        return getOrCreateResidentFromUUID(player.getUniqueId());
    }

    public Resident getOrCreateResidentFromUUID(UUID uuid) {
        if ( !residents.containsKey(uuid) ) return createResident(uuid);
        else return residents.get(uuid);
    }

    private Resident createResident(UUID uuid) {
        Resident resident = new Resident(uuid);
        residents.put(uuid, resident);
        return resident;
    }

    public static ResidentManager getInstance() {
        return instance;
    }

}
