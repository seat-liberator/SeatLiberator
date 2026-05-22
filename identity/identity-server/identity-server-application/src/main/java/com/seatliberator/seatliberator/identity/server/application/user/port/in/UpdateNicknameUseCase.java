package com.seatliberator.seatliberator.identity.server.application.user.port.in;

import com.seatliberator.seatliberator.identity.server.application.user.port.in.command.UpdateNicknameCommand;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.result.UserResult;

public interface UpdateNicknameUseCase {
    UserResult update(UpdateNicknameCommand command);
}
