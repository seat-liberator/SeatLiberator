package com.seatliberator.seatliberator.identity.server.application.account.port.in;

import com.seatliberator.seatliberator.identity.server.application.account.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.server.application.account.port.in.result.AuthEntry;

public interface UserRegistrar {
    AuthEntry register(RegistrationCommand.Credential command);

    AuthEntry register(RegistrationCommand.Federated command);
}
