package com.seatliberator.seatliberator.identity.api;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

public record DefaultNamespaceRoleGrant(
        ApplicationNamespace namespace,
        Role defaultRole
) implements NamespaceRole {
    @Override
    public Role role() {
        return defaultRole;
    }
}
