package com.atherys.towns.model.permission;

import com.atherys.core.database.api.DBObject;
import com.atherys.towns.model.Resident;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.spongepowered.api.util.Identifiable;

import java.util.Objects;
import java.util.UUID;

@Entity(value = "permissions", noClassnameStored = true)
public class ResidentPermission<T extends Identifiable> implements DBObject {

    @Id
    private UUID uuid;

    private PermissionPrototype<T> prototype;

    private Resident subject;

    private T context;

    public ResidentPermission(Resident subject, T context, PermissionPrototype<T> prototype) {
        this.uuid = UUID.randomUUID();
        this.prototype = prototype;
        this.subject = subject;
        this.context = context;
    }

    public Resident getSubject() {
        return subject;
    }

    public T getContext() {
        return context;
    }

    public String getId() {
        return prototype.getId();
    }

    public String getName() {
        return prototype.getName();
    }

    public String getPermission() {
        return prototype.getPermission();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidentPermission<?> that = (ResidentPermission<?>) o;
        return Objects.equals(prototype, that.prototype) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prototype, subject, context);
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
