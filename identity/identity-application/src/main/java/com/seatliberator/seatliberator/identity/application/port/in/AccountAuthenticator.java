package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;

public interface AccountAuthenticator {
    AuthEntry authenticate(AuthenticationCommand.Credential command);

    AuthEntry authenticate(AuthenticationCommand.Federated command);
}
