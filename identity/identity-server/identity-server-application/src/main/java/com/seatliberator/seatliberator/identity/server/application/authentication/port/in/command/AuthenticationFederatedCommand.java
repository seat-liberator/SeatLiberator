package com.seatliberator.seatliberator.identity.server.application.authentication.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record AuthenticationFederatedCommand(
        String registrationId,
        String providerUserId
) {
    public AuthenticationFederatedCommand {
        Preconditions.requireNonBlank(registrationId, "registrationId");
        Preconditions.requireNonBlank(providerUserId, "providerUserId");
    }

    public static AuthenticationFederatedCommand of(String registrationId, String providerUserId) {
        return new AuthenticationFederatedCommand(registrationId, providerUserId);
    }
}
