package com.seatliberator.seatliberator.identity.server.redis.token;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenRedisKeyManager {
    private final String keyPrefix;

    public String activeKey(String tokenHash) {
        Preconditions.requireNonBlank(tokenHash, "tokenHash");

        return keyPrefix + ":refresh-token:active:" + tokenHash;
    }

    public String revokedKey(String tokenHash) {
        Preconditions.requireNonBlank(tokenHash, "tokenHash");

        return keyPrefix + ":refresh-token:revoked:" + tokenHash;
    }

    public String familyRevokedKey(String familyId) {
        Preconditions.requireNonBlank(familyId, "familyId");

        return keyPrefix + ":refresh-token:family:revoked:" + familyId;
    }
}
