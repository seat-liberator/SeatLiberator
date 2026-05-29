package com.seatliberator.seatliberator.identity.server.application.token.port.in;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.CreateAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.result.AccessTokenResult;

public interface CreateAccessTokenUseCase {
    AccessTokenResult create(CreateAccessTokenCommand command);
}
