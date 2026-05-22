package com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record CredentialAccountEmailCriteria(String email) {
    public CredentialAccountEmailCriteria {
        Preconditions.requireNonBlank(email, "email");
    }

    public static CredentialAccountEmailCriteria of(String email) {
        return new CredentialAccountEmailCriteria(email);
    }
}
