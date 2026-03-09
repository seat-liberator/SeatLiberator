package com.seatliberator.seatliberator.jwks.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractKeyMetadata {
    private final String kid;
    private final KeyStatus status;

    public boolean canSign() {
        return status == KeyStatus.SIGNABLE;
    }

    public boolean canVerify() {
        return status == KeyStatus.VERIFY_ONLY || status == KeyStatus.SIGNABLE;
    }
}
