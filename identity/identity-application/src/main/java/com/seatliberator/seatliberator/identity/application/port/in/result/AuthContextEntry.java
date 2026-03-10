package com.seatliberator.seatliberator.identity.application.port.in.result;

import java.util.UUID;

public class AuthContextEntry {
    public record Credential(
            UUID userId,
            String email,
            String passwordHash
    ) {}

    public record Federated(
            UUID userId,
            String registrationId,
            String providerUserId
    ) {}
}
