package com.seatliberator.seatliberator.identity.server.application.account.port.in;

import com.seatliberator.seatliberator.identity.server.application.account.port.in.command.ExistenceCheckingCommand;

public interface AccountExistenceChecker {
    boolean isCredentialAccountExists(ExistenceCheckingCommand.Credential command);

    boolean isFederatedAccountExists(ExistenceCheckingCommand.Federated command);
}
