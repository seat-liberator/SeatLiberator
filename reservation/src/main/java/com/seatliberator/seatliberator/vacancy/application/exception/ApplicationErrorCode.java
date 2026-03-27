package com.seatliberator.seatliberator.vacancy.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {
    DUPLICATED_REQUEST("RVC001", "Duplicated vacancy alert request."),
    NOTIFICATION_ACCESS_DENIED("RVC002", "Only own notifications can be read.");

    private final String code;
    private final String message;
}
