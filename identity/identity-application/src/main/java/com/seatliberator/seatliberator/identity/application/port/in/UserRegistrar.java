package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.application.port.in.result.UserEntry;

public interface UserRegistrar {
    UserEntry register(RegistrationCommand.Credential command);

    UserEntry register(RegistrationCommand.Federated command);
}
