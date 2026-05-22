package com.seatliberator.seatliberator.identity.server.application.account.port.in;

import com.seatliberator.seatliberator.identity.server.application.account.port.in.command.ExistenceCheckingCommand;

public interface UserExistenceChecker {
    boolean isExists(ExistenceCheckingCommand.User command);
}
