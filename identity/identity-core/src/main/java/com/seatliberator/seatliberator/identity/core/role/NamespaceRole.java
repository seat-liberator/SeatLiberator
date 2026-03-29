package com.seatliberator.seatliberator.identity.core.role;

import com.seatliberator.seatliberator.kernel.ApplicationNamespace;

public interface NamespaceRole {
    ApplicationNamespace namespace();

    Role role();
}
