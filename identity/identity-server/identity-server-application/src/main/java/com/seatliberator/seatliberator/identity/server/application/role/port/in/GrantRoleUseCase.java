package com.seatliberator.seatliberator.identity.server.application.role.port.in;


import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.GrantRoleCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.result.UserGrantedRoleResult;

public interface GrantRoleUseCase {
    UserGrantedRoleResult grant(GrantRoleCommand command);
}

