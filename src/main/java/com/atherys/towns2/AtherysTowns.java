package com.atherys.towns2;

import static com.atherys.towns2.AtherysTowns.DESCRIPTION;
import static com.atherys.towns2.AtherysTowns.ID;
import static com.atherys.towns2.AtherysTowns.NAME;
import static com.atherys.towns2.AtherysTowns.VERSION;

import com.atherys.towns2.persistence.ResidentManager;
import java.util.Optional;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionService;

@Plugin(id = ID,
        name = NAME,
        description = DESCRIPTION,
        version = VERSION,
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "griefprevention")
        })
public class AtherysTowns {

    public final static String ID = "atherystowns";
    public final static String NAME = "A'therys Towns";
    public final static String DESCRIPTION = "A custom plugin responsible for agile land management. Created for the A'therys Horizons server.";
    public final static String VERSION = "1.0.0a";

    private static AtherysTowns instance;

    public static AtherysTowns getInstance() {
        return instance;
    }

    public static ResidentManager getResidentManager() {
        return ResidentManager.getInstance();
    }

    public static Optional<PermissionService> getPermissionService() {
        return Sponge.getServiceManager().provide(PermissionService.class);
    }

    public static LuckPermsApi getLuckPerms() {
        return LuckPerms.getApi();
    }
}
