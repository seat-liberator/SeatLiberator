package com.seatliberator.seatliberator.identity.application.port.out;

import java.util.UUID;

public class AuthRecord {
    public record Credential(
            UUID userId,
            String nickname,
            String email,
            String passwordHash
    ) {
    }

    public record Federated(
            UUID userId,
            String nickname,
            String registrationId,
            String providerUserId
    ) {
    }
}
