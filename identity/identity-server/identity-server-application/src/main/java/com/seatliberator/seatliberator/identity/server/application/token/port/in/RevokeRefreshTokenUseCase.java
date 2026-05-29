package com.seatliberator.seatliberator.identity.server.application.token.port.in;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RevokeRefreshTokenCommand;

public interface RevokeRefreshTokenUseCase {
    void revoke(RevokeRefreshTokenCommand command);
}
