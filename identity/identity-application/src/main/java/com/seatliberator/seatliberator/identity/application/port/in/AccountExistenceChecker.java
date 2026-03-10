package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.ExistenceCheckingCommand;

public interface AccountExistenceChecker {
    boolean isCredentialAccountExists(ExistenceCheckingCommand.Credential command);

    boolean isOIDCAccountExists(ExistenceCheckingCommand.Federated command);
}
