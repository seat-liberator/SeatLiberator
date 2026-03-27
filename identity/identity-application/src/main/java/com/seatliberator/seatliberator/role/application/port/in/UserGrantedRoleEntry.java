package com.seatliberator.seatliberator.role.application.port.in;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.role.domain.UserGrantedRole;

public record UserGrantedRoleEntry(
        String userId,
        String namespace,
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
