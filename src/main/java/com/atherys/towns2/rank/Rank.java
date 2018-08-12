package com.atherys.towns2.rank;

import com.atherys.towns2.AtherysTowns;
import com.atherys.towns2.base.ResidentContainer;
import com.atherys.towns2.rank.action.Action;
import com.atherys.towns2.resident.Resident;
import java.util.Map;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;
import org.spongepowered.api.util.Tristate;

public abstract class Rank<T extends Action> {

    private String id;
    private String name;

    private Group group;

    public Rank(String id, String name) {
        this.id = id;
        this.name = name;
        AtherysTowns.getLuckPerms()
            .getGroupManager()
            .createAndLoadGroup(id)
            .thenAcceptAsync(this::setGroup);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Tristate addAction(T action) {
        group.setPermission(action.getPermission());
    }

    public Tristate removeAction(T action) {
        group.unsetPermission(action.getPermission());
    }

    public boolean addResidentRank(ResidentContainer source, Resident resident) {
        resident.asUser().ifPresent(user -> {
            AtherysTowns.getLuckPerms().getGroupManager().getGroupOpt()
        });
    }

    public boolean removeResidentRank(ResidentContainer source, Resident resident) {

    }

    private void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}
