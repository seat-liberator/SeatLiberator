package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.server.domain.role.UserGrantedRole;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

public record UserGrantedRoleEntry(
        String userId,
        ApplicationNamespace namespace,
        Role role
) implements NamespaceRole {
    public static UserGrantedRoleEntry from(UserGrantedRole userGrantedRole) {
        return new UserGrantedRoleEntry(
                userGrantedRole.getUserId(),
                userGrantedRole.namespace(),
                userGrantedRole.role()
        );
    }
}
