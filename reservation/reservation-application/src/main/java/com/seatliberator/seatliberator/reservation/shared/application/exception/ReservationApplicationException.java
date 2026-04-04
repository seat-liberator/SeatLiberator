package com.seatliberator.seatliberator.reservation.shared.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;
import lombok.Getter;

@Getter
public class ReservationApplicationException extends ApplicationBaseException {
    private final ReservationApplicationErrorCode errorCode;

    public ReservationApplicationException(ReservationApplicationErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
