package com.atherys.towns.model;

import com.atherys.core.database.api.DBObject;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Town implements DBObject {

    private UUID uuid;

    private Text name;
    private Text description;

    private Plot homePlot;
    private Set<Plot> plots = new HashSet<>();

    private Resident leader;
    private Set<Resident> members = new HashSet<>();

    public Town(Text name, Text description, Resident leader, World world, Vector3i corner1, Vector3i corner2) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.homePlot = new Plot(world, corner1, corner2);
        this.plots.add(homePlot);
        this.leader = leader;
        this.members.add(leader);
    }

    public World getWorld() {
        return homePlot.getClaim().getWorld();
    }

    public Plot getHomePlot() {
        return homePlot;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public Set<Plot> getPlots() {
        return plots;
    }

    public Resident getLeader() {
        return leader;
    }

    public Set<Resident> getMembers() {
        return members;
    }
}
