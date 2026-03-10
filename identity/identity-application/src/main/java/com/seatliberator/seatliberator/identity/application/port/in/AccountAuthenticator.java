package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.AuthenticationCommand;
import com.seatliberator.seatliberator.identity.core.actor.Actor;

public interface AccountAuthenticator {
    Actor authenticate(AuthenticationCommand.Credential command);

    Actor authenticate(AuthenticationCommand.Federated command);
}
