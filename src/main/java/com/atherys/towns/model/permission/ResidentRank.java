package com.atherys.towns.model.permission;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An entity representing specific ranks within a town or a nation.
 * There always has to be rank with priority index of owner, other ranks are created by players
 *
 * ResidentRank has bidirectional binding between objects
 * Nation<>Resident
 * Town<>Resident
 *
 * On the resident object the Rank collection is a merged collection of all ranks the resident has received
 * Once the player joins into the server ResidentRank is translated to a datascructure as descriebed in ResidentAction
 *  and store within SubjectData in a transient scope
 */
@Entity(value = "rank", noClassnameStored = true)
public class ResidentRank implements Comparable<ResidentRank> {

    /**
     * This is the rank priority index number. This number is used for promotion/demotion of an user by any other user
     *
     * 0 is representing the town/nation owner.
     * it should guaranteed that if a town has one or more residents exactly one resident is the owner
     * Priority index is used for scanarios where resident with permission PROMOTE and rank priority index N
     * is able to promote another residents in his town up to rank N, or N+1
     */
    @Transient
    public static final transient int OWNER = 0;

    @Id
    private UUID UUID;

    private String name;

    private Set<ResidentAction> residentRights = new HashSet<>();

    private PermissionContext permissionContextSource;

    private int priorityIndex;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUUID(UUID UUID) {
        this.UUID = UUID;
    }

    public UUID getUUID() {
        return UUID;
    }

    public void setPriorityIndex(int priorityIndex) {
        this.priorityIndex = priorityIndex;
    }

    public int getPriorityIndex() {
        return priorityIndex;
    }

    public Set<ResidentAction> getResidentRights() {
        return residentRights;
    }

    public void setResidentRights(Set<ResidentAction> residentRights) {
        this.residentRights = residentRights;
    }

    public PermissionContext getPermissionContext() {
        return permissionContextSource;
    }

    public void setPermissionContextSource(PermissionContext permissionContextSource) {
        this.permissionContextSource = permissionContextSource;
    }

    @Override
    public int compareTo(ResidentRank o) {
        return Integer.compare(priorityIndex, o.priorityIndex);
    }
}
