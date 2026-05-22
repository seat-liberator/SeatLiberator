package com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record FederatedAccountUserRegistrationLookupCriteria(
        UUID userId,
        String registrationId
) {
    public FederatedAccountUserRegistrationLookupCriteria {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonBlank(registrationId, "registrationId");
    }

    public static FederatedAccountUserRegistrationLookupCriteria of(UUID userId, String registrationId) {
        return new FederatedAccountUserRegistrationLookupCriteria(userId, registrationId);
    }
}
