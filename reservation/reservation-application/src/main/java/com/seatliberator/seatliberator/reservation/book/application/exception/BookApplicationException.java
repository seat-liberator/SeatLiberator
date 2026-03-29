package com.seatliberator.seatliberator.reservation.book.application.exception;

import com.seatliberator.seatliberator.kernel.exception.ApplicationBaseException;
import lombok.Getter;

@Getter
public class BookApplicationException extends ApplicationBaseException {
    private final BookApplicationErrorCode errorCode;

    public BookApplicationException(BookApplicationErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
