package com.seatliberator.seatliberator.identity.core.role;

import java.util.Optional;

public interface NamespaceRoleDeserializer {
    Optional<NamespaceRole> tryMaterialize(String raw);
}
