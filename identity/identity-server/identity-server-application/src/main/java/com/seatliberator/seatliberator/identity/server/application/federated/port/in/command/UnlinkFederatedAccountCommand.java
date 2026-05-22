package com.seatliberator.seatliberator.identity.server.application.federated.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UnlinkFederatedAccountCommand(
        UUID userId,
        String registrationId
) {
    public UnlinkFederatedAccountCommand {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(registrationId, "registrationId");
    }

    public static UnlinkFederatedAccountCommand of(UUID userId, String registrationId) {
        return new UnlinkFederatedAccountCommand(userId, registrationId);
    }
}
