package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.AuthEntry;

public interface UserRegistrar {
    AuthEntry register(RegistrationCommand.Credential command);

    AuthEntry register(RegistrationCommand.Federated command);
}
