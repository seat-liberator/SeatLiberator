package com.seatliberator.seatliberator.kernel.exception;

import java.util.Objects;

public abstract class ApplicationBaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApplicationBaseException(ErrorCode errorCode) {
        super(Objects.requireNonNull(errorCode).getMessage());
        this.errorCode = errorCode;
    }

    public ApplicationBaseException(ErrorCode errorCode, Throwable cause) {
        super(Objects.requireNonNull(errorCode).getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
