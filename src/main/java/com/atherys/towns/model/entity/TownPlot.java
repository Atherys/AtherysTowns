package com.atherys.towns.model.entity;

import com.atherys.towns.persistence.converter.TextConverter;
import org.spongepowered.api.text.Text;

import javax.persistence.*;
import java.util.Set;

@Entity
public class TownPlot extends Plot {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "town_id")
    private Town town;

    @Convert(converter = TextConverter.class)
    private Text name;

    @ManyToOne(fetch = FetchType.EAGER)
    private Resident owner;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "townplot_permissions",
            joinColumns = @JoinColumn(name = "townplot_id"),
            inverseJoinColumns = @JoinColumn(name = "townplot_permission_id")
    )
    private Set<TownPlotPermission> permissions;

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Resident getOwner() {
        return owner;
    }

    public void setOwner(Resident owner) {
        this.owner = owner;
    }

    public Set<TownPlotPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<TownPlotPermission> permissions) {
        this.permissions = permissions;
    }
}
