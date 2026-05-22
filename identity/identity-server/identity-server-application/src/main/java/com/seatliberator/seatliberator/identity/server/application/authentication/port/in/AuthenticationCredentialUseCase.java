package com.seatliberator.seatliberator.identity.server.application.authentication.port.in;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command.AuthenticationCredentialCommand;
import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;

public interface AuthenticationCredentialUseCase {
    AuthenticatedResult authenticate(AuthenticationCredentialCommand command);
}
