package com.seatliberator.seatliberator.jwks.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;
import java.util.Set;

@Slf4j
@ConfigurationProperties(prefix = "identity.jwks")
public record JWKSProperties(
        String signableKid,
        Set<KeyEntry> keys
) {
    public record KeyEntry(
            String kid,
            String privateKey,
            String publicKey
    ) {
    }

    public JWKSProperties {
        signableKid = signableKid != null ? signableKid : "";
        keys = keys != null ? keys : Set.of();

        var availableKids = keys.stream().map(KeyEntry::kid).toList();

        log.debug("Available key entries id: {}, Signable key id: {}", availableKids, signableKid);

        if (keys.stream().noneMatch(keyEntry -> Objects.equals(keyEntry.kid(), signableKid()))) {
            log.debug("There is no key matching the key id of a signable key among the provided key sets.");
        }
    }

    public boolean isConfigured() {
        return !signableKid.isBlank() && !keys.isEmpty();
    }
}
