package com.atherys.towns.api.command;

import com.atherys.towns.AtherysTowns;
import com.atherys.towns.api.command.exception.TownsCommandException;
import com.atherys.towns.api.permission.Permission;
import com.atherys.towns.api.permission.Subject;
import com.atherys.towns.entity.Resident;
import com.atherys.towns.service.PermissionService;
import com.atherys.towns.service.ResidentService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public interface TownsCommand extends CommandExecutor {

    @Override
    default CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof User) {
            PermissionService permissionService = AtherysTowns.getInstance().getPermissionService();
            ResidentService residentService = AtherysTowns.getInstance().getResidentService();

            Resident resident = residentService.getOrCreate((User) src);

            Subject subject = getSubject(src, resident, args);

            Permission permission = getPermission();

            if (permissionService.isPermitted(resident, subject, permission)) {
                return execute(resident, subject, args);
            } else {
                throw new TownsCommandException(Text.of("You are not allowed to ", permission.getName()));
            }
        }

        throw new TownsCommandException(Text.of("Must be player to execute this command."));
    }

    CommandResult execute(Resident resident, Subject subject, CommandContext args);

    Subject getSubject(CommandSource source, Resident resident, CommandContext args);

    Permission getPermission();
}
