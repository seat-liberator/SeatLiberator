package com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record FederatedAccountLookupCriteria(
        String registrationId,
        String providerUserId
) {
    public FederatedAccountLookupCriteria {
        Preconditions.requireNonBlank(registrationId, "registrationId");
        Preconditions.requireNonBlank(providerUserId, "providerUserId");
    }

    public static FederatedAccountLookupCriteria of(String registrationId, String providerUserId) {
        return new FederatedAccountLookupCriteria(registrationId, providerUserId);
    }
}
