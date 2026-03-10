package com.seatliberator.seatliberator.identity.application.port.out;

import java.util.UUID;

public class AuthContextRecord {
    public record Credential(
            UUID userId,
            String email,
            String passwordHash
    ) {
    }

    public record Federated(
            UUID userId,
            String registrationId,
            String providerUserId
    ) {
    }
}
