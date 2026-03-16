package com.seatliberator.seatliberator.identity.application.port.in;

import com.seatliberator.seatliberator.identity.application.port.in.command.ExistenceCheckingCommand;

public interface UserExistenceChecker {
    boolean isExists(ExistenceCheckingCommand.User command);
}
