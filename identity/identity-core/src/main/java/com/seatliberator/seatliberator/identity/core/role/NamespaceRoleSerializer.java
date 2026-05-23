package com.seatliberator.seatliberator.identity.core.role;

import java.util.Collection;
import java.util.Set;

public interface NamespaceRoleSerializer {
    String serialize(NamespaceRole namespaceRole);

    Set<String> serialize(Collection<NamespaceRole> namespaceRoles);
}
