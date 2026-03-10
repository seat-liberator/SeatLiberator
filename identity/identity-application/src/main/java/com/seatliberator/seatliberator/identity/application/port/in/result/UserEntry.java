package com.seatliberator.seatliberator.identity.application.port.in.result;

import com.seatliberator.seatliberator.identity.domain.User;

import java.util.UUID;

public record UserEntry(
        UUID userId,
        String nickname
) {
    public static UserEntry of(User user) {
        return new UserEntry(
                user.getId(),
                user.getNickname()
        );
    }
}
