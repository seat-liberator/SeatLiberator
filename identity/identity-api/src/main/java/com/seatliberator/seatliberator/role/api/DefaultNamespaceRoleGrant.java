package com.seatliberator.seatliberator.role.api;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;

public record DefaultNamespaceRoleGrant(
        String namespace,
        Role defaultRole
) implements NamespaceRole {
    @Override
    public Role role() {
        return defaultRole;
    }
}
