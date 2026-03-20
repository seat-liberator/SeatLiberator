package com.seatliberator.seatliberator.identity.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;

public class IdentityApplicationException extends ApplicationBaseException {
    public IdentityApplicationException(IdentityApplicationErrorCode errorCode) {
        super(errorCode);
    }
}
