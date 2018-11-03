package com.atherys.towns.model.permission;

import static com.atherys.towns.model.permission.PermissionContext.Type.TOWN;

/**
 * List of Resident rights
 *
 * Permission nodes are dynamically created at runtime depending on a context.
 *
 * For example user, which may send invitations
 * Action: INVITE_MEMBER
 *
 * This action may be used in all permission Contexts.
 * Depending on the context residentaction will generate different permission node
 * - PLOT: Invites player to a plot
 *    - at.invite.plot.<uuid>
 * - TOWN: Invites player to a town
 *    - at.invite.town.<uuid>
 * - NATION: Invites town to a nation
 *    - at.invite.nation.<uuid>
 *
 * Usage:
 * - player.hasPermission(nation.getPermissionNodeForAction(ResidentRights.INVITE_MEMBER)))...
 *
 * LuckPerms API has limit only for a SQL-based storage - 200 characters. UUID is always 36 characters so we are fine
 *
 * Eventually this could be easilly converted to permission-metadata storage such as
 *
 * - at.invite.plot/<uuid> But that would decrease the flexibility, since there may be only one metadata value per player per permission node
 */
public class ResidentRights {

    public static final ResidentAction INVITE_MEMBER =
            new ResidentAction("at.invite", PermissionContext.Type.values());

    public static final ResidentAction PROMOTE_RESIDENT =
            new ResidentAction("at.user.promote", PermissionContext.Type.values());

    public static final ResidentAction DEMOTE_RESIDENT =
            new ResidentAction("at.user.demote", PermissionContext.Type.values());

    public static final ResidentAction GRANT_PLOT_PERMISSIONS =
            new ResidentAction("at.townplot.create", TOWN);

    public static final ResidentAction KICK_MEMBER =
            new ResidentAction("at.kick", PermissionContext.Type.values());

    public static final ResidentAction BUILD =
            new ResidentAction("at.area.edit", PermissionContext.Type.values());

    public static final ResidentAction DESTROY = new ResidentAction("at.area.destroy", PermissionContext.Type.values());

    public static final ResidentAction DAMAGE_ENTITY = new ResidentAction("at.area.pve", PermissionContext.Type.values());;

    public static final ResidentAction DAMAGE_PLAYER = new ResidentAction("at.area.pvp", PermissionContext.Type.values());;

    public static final ResidentAction INTERACT_WITH_DOORS = new ResidentAction("at.area.interact.doors", PermissionContext.Type.values());

    public static final ResidentAction INTERACT_WITH_OTHER_WEIRD_STUFF = new ResidentAction("at.area.interact.redstone", PermissionContext.Type.values());

}
