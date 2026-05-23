package com.seatliberator.seatliberator.identity.server.application.role.port.out.criteria;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.UUID;

public record UserGrantedRoleUserNamespaceCriteria(
        UUID userId,
        ApplicationNamespace namespace
) {
    public UserGrantedRoleUserNamespaceCriteria {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonNull(namespace, "namespace");
    }

    public static UserGrantedRoleUserNamespaceCriteria of(UUID userId, ApplicationNamespace namespace) {
        return new UserGrantedRoleUserNamespaceCriteria(userId, namespace);
    }
}
