package com.seatliberator.seatliberator.identity.server.application.token.port.out;

public enum RevokedRefreshTokenReason {
    ROTATED,
    LOGOUT,
    REUSE_DETECTED,
    ADMIN_REVOKED
}
