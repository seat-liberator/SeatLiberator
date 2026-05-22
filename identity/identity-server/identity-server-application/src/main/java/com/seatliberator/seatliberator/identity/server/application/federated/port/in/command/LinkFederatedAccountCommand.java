package com.seatliberator.seatliberator.identity.server.application.federated.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record LinkFederatedAccountCommand(
        UUID userId,
        String registrationId,
        String providerUserId
) {
    public LinkFederatedAccountCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(registrationId, "registrationId");
        Preconditions.requireNonBlank(providerUserId, "providerUserId");
    }

    public static LinkFederatedAccountCommand of(UUID userId, String registrationId, String providerUserId) {
        return new LinkFederatedAccountCommand(userId, registrationId, providerUserId);
    }
}
