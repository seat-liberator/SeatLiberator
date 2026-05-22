package com.seatliberator.seatliberator.identity.server.application.federated.port.in.command;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record RegisterFederatedAccountCommand(
        String registrationId,
        String providerUserId,
        String providerUserNickname
) {
    public RegisterFederatedAccountCommand {
        Preconditions.requireNonBlank(registrationId, "registrationId");
        Preconditions.requireNonBlank(providerUserId, "providerUserId");
        Preconditions.requireNonBlank(providerUserNickname, "providerUserNickname");
    }

    public static RegisterFederatedAccountCommand of(String registrationId, String providerUserId, String providerUserNickname) {
        return new RegisterFederatedAccountCommand(registrationId, providerUserId, providerUserNickname);
    }
}
