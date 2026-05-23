package com.seatliberator.seatliberator.identity.server.application.role.port.in.result;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

import java.util.List;
import java.util.UUID;

public record UserGrantedSummaryResult(
        UUID userId,
        List<NamespaceRoleResult> grants
) {
    public UserGrantedSummaryResult {
        Preconditions.requireNonNull(userId, "userId");
        Preconditions.requireNonNull(grants, "grants");
    }

    public static UserGrantedSummaryResult of(UUID userId, List<NamespaceRoleResult> namespaceRoles) {
        return new UserGrantedSummaryResult(userId, List.copyOf(namespaceRoles));
    }
}
