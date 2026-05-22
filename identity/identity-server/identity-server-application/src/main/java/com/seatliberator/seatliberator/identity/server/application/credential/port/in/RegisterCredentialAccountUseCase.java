package com.seatliberator.seatliberator.identity.server.application.credential.port.in;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.RegisterCredentialAccountCommand;

public interface RegisterCredentialAccountUseCase {
    AuthenticatedResult register(RegisterCredentialAccountCommand command);
}
