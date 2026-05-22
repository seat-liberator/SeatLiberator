package com.seatliberator.seatliberator.identity.server.application.federated.port.in;

import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.LinkFederatedAccountCommand;

public interface LinkFederatedAccountUseCase {
    void link(LinkFederatedAccountCommand command);
}
