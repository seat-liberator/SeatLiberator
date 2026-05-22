package com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record CredentialAccountUserCriteria(UUID userId) {
    public CredentialAccountUserCriteria {
        Preconditions.requireNonNull(userId, "userId");
    }

    public static CredentialAccountUserCriteria of(UUID userId) {
        return new CredentialAccountUserCriteria(userId);
    }
}
