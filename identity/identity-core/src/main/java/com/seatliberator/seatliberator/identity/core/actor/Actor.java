package com.seatliberator.seatliberator.identity.core.actor;

import java.util.Set;

public interface Actor {
    String subject();

    Set<String> scopes();
}
