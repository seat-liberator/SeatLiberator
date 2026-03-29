package com.seatliberator.seatliberator.reservation.vacancy.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;
import lombok.Getter;

@Getter
public class VacancyApplicationException extends ApplicationBaseException {
    private final VacancyApplicationErrorCode errorCode;

    public VacancyApplicationException(VacancyApplicationErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
