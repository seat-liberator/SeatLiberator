package com.seatliberator.seatliberator.identity.application.port.in.command;

import org.jspecify.annotations.Nullable;

public class ExistenceCheckingCommand {
    public record Credential(
            String email
    ) {
    }

    public record Federated(
            String registrationId,
            String providerUserId
    ) {
    }

    public record User(
            @Nullable String subject,
            @Nullable String email
    ) {
    }
}
