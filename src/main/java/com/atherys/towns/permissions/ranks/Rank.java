package com.atherys.towns.permissions.ranks;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.permissions.actions.TownsAction;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

public abstract class Rank {

  protected Rank child;
  protected SubjectReference permissions;
  private String id;
  private String name;

  protected Rank(String id, String name, List<? extends TownsAction> permittedActions,
      Rank child) {
    this.id = id;
    this.name = name;

    PermissionService service = AtherysTowns.getPermissionService();

    permissions = service.getGroupSubjects().newSubjectReference(id);
    permissions.resolve().thenAccept(subject -> {
      for (TownsAction action : permittedActions) {
        subject.getSubjectData()
            .setPermission(new LinkedHashSet<>(), action.getPermission(), Tristate.TRUE);
      }

      subject.getSubjectData().addParent(new LinkedHashSet<>(),
          service.getGroupSubjects().newSubjectReference("atherystowns"));
    });
    this.child = child;
  }

  public void addPermissions(User player) {
    player.getSubjectData().addParent(new LinkedHashSet<>(), permissions);
  }

  public void updatePermissions(UUID uuid, Rank newRank) {
    AtherysTowns.getPermissionService().getUserSubjects().loadSubject(uuid.toString())
        .thenAccept(subject -> {
          subject.getSubjectData().removeParent(new LinkedHashSet<>(), this.permissions);
          subject.getSubjectData().addParent(new LinkedHashSet<>(), newRank.permissions);
        });
  }

  public void removePermissions(User player) {
    player.getSubjectData().removeParent(new LinkedHashSet<>(), permissions);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Rank getChild() {
    return child;
  }

  public boolean isRankGreaterThan(Rank rank) {
    return this.getChild() != null && (this.getChild() == rank || this.getChild()
        .isRankGreaterThan(rank));
  }
}
