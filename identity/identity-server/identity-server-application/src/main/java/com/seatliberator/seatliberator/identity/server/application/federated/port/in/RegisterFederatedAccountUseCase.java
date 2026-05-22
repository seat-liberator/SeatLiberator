package com.seatliberator.seatliberator.identity.server.application.federated.port.in;

import com.seatliberator.seatliberator.identity.server.application.authentication.port.in.result.AuthenticatedResult;
import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.RegisterFederatedAccountCommand;

public interface RegisterFederatedAccountUseCase {
    AuthenticatedResult register(RegisterFederatedAccountCommand command);
}
