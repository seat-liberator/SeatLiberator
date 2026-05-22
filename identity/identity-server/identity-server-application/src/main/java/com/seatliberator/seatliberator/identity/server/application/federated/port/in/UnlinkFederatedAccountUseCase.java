package com.seatliberator.seatliberator.identity.server.application.federated.port.in;

import com.seatliberator.seatliberator.identity.server.application.federated.port.in.command.UnlinkFederatedAccountCommand;

public interface UnlinkFederatedAccountUseCase {
    void unlink(UnlinkFederatedAccountCommand command);
}
