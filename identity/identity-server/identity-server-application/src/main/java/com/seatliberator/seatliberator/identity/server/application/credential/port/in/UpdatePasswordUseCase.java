package com.seatliberator.seatliberator.identity.server.application.credential.port.in;

import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.UpdatePasswordCommand;

public interface UpdatePasswordUseCase {
    void update(UpdatePasswordCommand command);
}
