package com.seatliberator.seatliberator.identity.client.role;

import com.seatliberator.seatliberator.identity.core.role.Role;

import java.util.Set;

public record RoleCapabilities(
        Role role,
        Set<Capability> capabilities
) {
}