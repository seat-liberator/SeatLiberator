package com.seatliberator.seatliberator.identity.server.application.federated.port.out.filter;

import org.jspecify.annotations.Nullable;

public record FederatedAccountFilter(
        @Nullable String registrationId
) {
    public static FederatedAccountFilter empty() {
        return new FederatedAccountFilter(null);
    }

    public FederatedAccountFilter registrationId(String registrationId) {
        return new FederatedAccountFilter(registrationId);
    }
}
