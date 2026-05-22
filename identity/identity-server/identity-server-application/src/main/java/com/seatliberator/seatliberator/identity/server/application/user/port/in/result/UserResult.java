package com.seatliberator.seatliberator.identity.server.application.user.port.in.result;

import com.seatliberator.seatliberator.identity.server.domain.account.User;

import java.time.Instant;
import java.util.UUID;

public record UserResult(
        UUID userId,
        String nickname,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getNickname(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
