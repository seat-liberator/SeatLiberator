package com.seatliberator.seatliberator.identity.server.application.token.port.in;

import com.seatliberator.seatliberator.identity.server.application.token.port.in.command.RefreshAccessTokenCommand;
import com.seatliberator.seatliberator.identity.server.application.token.port.in.result.AccessTokenResult;

public interface RefreshAccessTokenUseCase {
    AccessTokenResult refresh(RefreshAccessTokenCommand command);
}
