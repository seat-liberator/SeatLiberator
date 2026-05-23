package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.RevokeRoleCommand;

public interface RevokeRoleUseCase {
    void revoke(RevokeRoleCommand command);
}
