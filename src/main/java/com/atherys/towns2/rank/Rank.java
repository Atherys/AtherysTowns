package com.atherys.towns2.rank;

import com.atherys.towns2.base.ResidentContainer;
import com.atherys.towns2.rank.action.Action;
import com.atherys.towns2.resident.Resident;
import com.google.common.collect.Sets;
import java.util.Map;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.ContextSource;
import org.spongepowered.api.util.Tristate;

public abstract class Rank<T extends Action> implements ContextSource{

    private String id;
    private String name;

    private Map<T, Tristate> actions;

    public Rank(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<T, Tristate> getActions() {
        return actions;
    }

    public Tristate get(T action) {
        return actions.get(action);
    }

    public Tristate addAction(T action, Tristate value) {
        return actions.put(action, value);
    }

    public Tristate removeAction(T action) {
        return actions.remove(action);
    }

    public boolean addResidentRank(ResidentContainer source, Resident resident) {

        User user = resident.asUser().orElse(null);
        if ( user == null ) return false;

        actions.forEach((action,value) -> {
            user.getSubjectData().setPermission(
                Sets.newHashSet(source.getContext(), this.getContext()),
                action.getPermission(),
                value
            );
        });

        return true;
    }

    public boolean removeResidentRank(ResidentContainer source, Resident resident) {
        User user = resident.asUser().orElse(null);
        if ( user == null ) return false;

        user.getSubjectData().clearPermissions(Sets.newHashSet(source.getContext(), this.getContext()));

        return true;
    }
}
