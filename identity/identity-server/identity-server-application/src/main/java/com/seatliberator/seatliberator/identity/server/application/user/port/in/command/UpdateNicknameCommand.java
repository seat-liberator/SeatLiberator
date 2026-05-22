package com.seatliberator.seatliberator.identity.server.application.user.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UpdateNicknameCommand(
        UUID userId,
        String nickname
) {
    public UpdateNicknameCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(nickname, "nickname");
    }

    public static UpdateNicknameCommand of(UUID userId, String nickname) {
        return new UpdateNicknameCommand(userId, nickname);
    }
}
