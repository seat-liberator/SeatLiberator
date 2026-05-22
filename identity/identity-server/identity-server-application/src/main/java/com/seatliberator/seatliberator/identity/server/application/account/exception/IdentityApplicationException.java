package com.seatliberator.seatliberator.identity.server.application.account.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;

public class IdentityApplicationException extends ApplicationBaseException {
    public IdentityApplicationException(IdentityApplicationErrorCode errorCode) {
        super(errorCode);
    }

    public IdentityApplicationException(IdentityApplicationErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    @Override
    public IdentityApplicationErrorCode getErrorCode() {
        return (IdentityApplicationErrorCode) super.getErrorCode();
    }
}
