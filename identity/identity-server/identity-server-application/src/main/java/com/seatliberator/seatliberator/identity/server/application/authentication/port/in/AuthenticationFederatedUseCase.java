package com.seatliberator.seatliberator.identity.server.application.authentication.port.in;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationFederatedCommand;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;

public interface AuthenticationFederatedUseCase {
    AuthenticatedResult authenticate(AuthenticationFederatedCommand command);
}
