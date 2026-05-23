package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.identity.core.actor.Capability;

import java.util.Set;

public record RoleCapabilities(
        Role role,
        Set<Capability> capabilities
) {
}