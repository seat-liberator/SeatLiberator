package com.seatliberator.seatliberator.identity.server.application.account.port.in;

import com.seatliberator.seatliberator.identity.server.application.account.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.server.application.account.port.in.result.AuthEntry;

public interface AccountAuthenticator {
    AuthEntry authenticate(AuthenticationCommand.Credential command);

    AuthEntry authenticate(AuthenticationCommand.Federated command);
}
