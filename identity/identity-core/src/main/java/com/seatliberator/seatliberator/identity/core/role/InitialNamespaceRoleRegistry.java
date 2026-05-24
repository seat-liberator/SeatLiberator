package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

import java.util.Collection;

public interface InitialNamespaceRoleRegistry {
    Collection<NamespaceRole> getAll();

    NamespaceRole resolveByNamespace(ApplicationNamespace namespace);
}
