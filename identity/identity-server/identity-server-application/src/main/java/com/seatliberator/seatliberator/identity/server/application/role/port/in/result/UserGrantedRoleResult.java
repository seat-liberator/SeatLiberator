package com.seatliberator.seatliberator.identity.server.application.role.port.in.result;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;

import java.util.UUID;

public record UserGrantedRoleResult(
        UUID grantedRoleId,
        UUID userId,
        NamespaceRole namespaceRole
) {
    public static UserGrantedRoleResult from(UserGrantedRole userGrantedRole) {
        return new UserGrantedRoleResult(
                userGrantedRole.getId(),
                userGrantedRole.getUserId(),
                SimpleNamespaceRole.copyOf(userGrantedRole.getNamespaceRole())
        );
    }
}
