package com.seatliberator.seatliberator.identity.core.role;

import java.util.Collection;
import java.util.Set;

public interface NamespaceRoleDeserializer {
    NamespaceRole materialize(String raw);

    Set<NamespaceRole> materialize(Collection<String> rawCollection);
}
