package com.seatliberator.seatliberator.identity.core.role;

import java.util.Collection;
import java.util.Set;

public interface NamespaceRoleFormatter {
    String format(NamespaceRole namespaceRole);

    Set<String> format(Collection<NamespaceRole> namespaceRoles);
}
