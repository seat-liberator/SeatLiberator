package com.seatliberator.seatliberator.reservation.verification.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;
import lombok.Getter;

@Getter
public class VerificationApplicationException extends ApplicationBaseException {
    private final VerificationApplicationErrorCode errorCode;

    public VerificationApplicationException(VerificationApplicationErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
