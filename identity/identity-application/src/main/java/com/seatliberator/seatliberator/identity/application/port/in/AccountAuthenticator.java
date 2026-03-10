package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthContextEntry;

public interface AccountAuthenticator {
    AuthContextEntry.Credential authenticate(AuthenticationCommand.Credential command);

    AuthContextEntry.Federated authenticate(AuthenticationCommand.Federated command);
}
