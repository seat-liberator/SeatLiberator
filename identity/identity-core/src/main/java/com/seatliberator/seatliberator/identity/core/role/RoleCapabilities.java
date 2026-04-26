package com.seatliberator.seatliberator.identity.core.role;

import java.util.Set;

public record RoleCapabilities(
        Role role,
        Set<Capability> capabilities
) {
}