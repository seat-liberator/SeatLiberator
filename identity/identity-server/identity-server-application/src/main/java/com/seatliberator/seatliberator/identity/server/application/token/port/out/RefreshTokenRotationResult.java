package com.seatliberator.seatliberator.identity.server.application.token.port.out;

public enum RefreshTokenRotationResult {
    SUCCESS,
    OLD_TOKEN_NOT_FOUND,
    NEW_TOKEN_CONFLICT
}
