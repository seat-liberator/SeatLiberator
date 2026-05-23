package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.UpdateRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedRoleResult;

public interface UpdateRoleUseCase {
    UserGrantedRoleResult update(UpdateRoleCommand command);
}
