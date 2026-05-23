package com.seatliberator.seatliberator.identity.core.actor;

import com.seatliberator.seatliberator.identity.core.role.Capability;

import java.util.Set;

public interface Actor {
    String subject();

    Set<Capability> capabilities();
}
